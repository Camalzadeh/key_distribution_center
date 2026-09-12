# Setup guide

Everything runs in containers. You do not need a JDK, Maven or PostgreSQL installed — only
Docker with the Compose plugin, which is included in Docker Desktop and in recent Docker
Engine packages.

```bash
docker --version
docker compose version
```

---

## 1. First run

```bash
cp .env.example .env
```

Open `.env` and set `POSTGRES_PASSWORD` to something of your own. The example file ships a
placeholder rather than a working value on purpose — a password a reader can copy and run
unchanged becomes the password everybody uses. `.env` is excluded from git and from the Docker
build context, by name and by shape.

```bash
docker compose up --build
```

The first build takes a few minutes, almost all of it Maven resolving dependencies. That layer
is keyed on `pom.xml` alone, so later builds after a code change take seconds.

Two containers come up:

| | Address | Notes |
|---|---|---|
| Application | <http://localhost:8080> | |
| PostgreSQL | `localhost:5432` | bound to loopback only |

Wait for the application log to settle on a line like
`Tomcat started on port 8080` before loading the page. The database has to accept a connection
and Hibernate has to create its tables first, and `docker compose up` starts printing well
before that has happened.

## 2. Create an account

There is one form for sign-in and sign-up: a username that does not exist yet is registered,
and an RSA key pair is generated for it immediately. An existing username is authenticated.

To send a message you need two accounts. Register the second one in a **different browser
profile or a private window** — not another tab. The session is a cookie, so a second tab in
the same profile signs you out of the first account rather than giving you two.

Then: **Compose** → pick the recipient, a cipher and a key → send. Open **Messages** and
expand *"what is actually stored"* on the message to see the ciphertext, the RSA-wrapped
symmetric key, and the key after unwrapping.

### Keys have to match the cipher

`Key.giveKey` chooses the key type from the algorithm, so the wrong shape is rejected rather
than coerced:

| Cipher | Key |
|---|---|
| Caesar | a whole number, e.g. `3` |
| Rail Fence | a whole number, e.g. `4` |
| Playfair | letters only, e.g. `MONARCHY` |
| Vigenère | anything, e.g. `LEMONBASIL` |

The forms show the requirement for whichever cipher is selected and update it when you switch.
A rejected key now reports why; it used to redisplay the form with no message at all.

## 3. Everyday commands

```bash
docker compose logs -f app     # follow the application
docker compose logs -f db      # follow PostgreSQL
docker compose restart app     # restart after an env change
docker compose up --build app  # rebuild after a code change
docker compose down            # stop, keep the data
docker compose down -v         # stop and discard the database volume
```

To reach the database directly:

```bash
docker compose exec db psql -U "$POSTGRES_USER" -d "$POSTGRES_DB"
```

## 4. Configuration

Every setting is read from the environment, with the defaults and the reasoning written into
[`../src/main/resources/application.properties`](../src/main/resources/application.properties).
The ones worth knowing:

| Variable | Default | What it does |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/kdc` | JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | database user |
| `SPRING_DATASOURCE_PASSWORD` | *none* | **no default** — startup fails without it |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` | schema handling at start-up |
| `SPRING_JPA_SHOW_SQL` | `false` | logs every statement, values included |
| `COOKIE_SECURE` | `true` | send the session cookie only over HTTPS |
| `SESSION_TTL_HOURS` | `12` | session lifetime |
| `MANAGEMENT_PORT` | `8081` | health endpoint, bound to `127.0.0.1` |

`SPRING_DATASOURCE_PASSWORD` having no default is deliberate. It used to fall back to a real
password written into a tracked file — which in a public repository means a published password.
Failing loudly is better than connecting as something unintended.

`COOKIE_SECURE` is the one you will hit locally: over plain `http://localhost` a browser
discards a `Secure` cookie, so sign-in appears to succeed and then bounces straight back to
`/auth`. The compose file sets it to `false` for exactly that reason. Anywhere with HTTPS in
front should leave it at `true`.

## 5. Health

The application exposes `GET /actuator/health` on its own port — `8081` by default, bound to
`127.0.0.1` **inside the container**. It is not published and cannot be reached from outside;
the image's `HEALTHCHECK` is what reads it.

`UP` means a connection was checked out of the pool, so it is a real round trip rather than
"the process is alive". To see it yourself:

```bash
docker compose exec app wget -q -O- http://127.0.0.1:8081/actuator/health
docker compose ps                  # the container's own health column
```

Being on a separate port keeps it out of the request path entirely: a management port gets its
own child context, so the session filter never runs against it and the probe is never answered
with a redirect to the sign-in page.

## 6. When something is wrong

| Symptom | Cause | Fix |
|---|---|---|
| `set POSTGRES_PASSWORD in .env` on startup | no `.env`, or the variable is missing | `cp .env.example .env`, then edit it |
| App exits with a connection refused | it outran the database | it restarts on its own; `docker compose logs db` if it does not |
| Sign-in succeeds then returns to `/auth` | a `Secure` cookie over plain HTTP | `COOKIE_SECURE=false` — already set in compose |
| Container never reports `healthy` | database unreachable, or the schema pass failed | `docker compose logs app`; the health probe needs a live connection |
| `port is already allocated` | something else holds 8080 or 5432 | stop it, or change the left-hand side of the port mapping |
| A cipher returns nothing and the form comes back | the key shape is wrong for the cipher | read the message above the form, and §2 |
| Decryption returns nonsense with no error | key or block mode does not match what encrypted it | transposition and substitution both "succeed" on wrong input |

For a clean slate:

```bash
docker compose down -v
docker compose up --build
```

## 7. Deploying it

The image is built and published by
[`.github/workflows/image.yml`](../.github/workflows/image.yml), and whatever runs the
application pulls that finished image — nothing is compiled at deploy time. The workflow runs
the tests, asserts the image contains no credentials and does not run as root, and starts it
against a real database before publishing.

A push to `master` publishes `latest` and a `sha-` tag. `prod` moves only when the workflow is
run by hand with `release=true`, because Hibernate applies the schema when the container
starts: deploying every commit automatically would mean applying every schema change
automatically, and that is the step that putting the old image back does not undo.

Runtime configuration — addresses, credentials, the compose file that actually runs — lives
with the deployment rather than in this repository, so nothing here has to be edited to move
it. [`../render.yaml`](../render.yaml) is an earlier blueprint for a different host, kept for
reference and not maintained; its known limitation is noted at the top of the file.
