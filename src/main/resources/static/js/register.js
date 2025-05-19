const themeToggle = document.getElementById('themeToggle');
const body = document.body;

const savedTheme = localStorage.getItem('theme');
if (savedTheme === 'dark') {
    body.classList.add('dark-mode');
    themeToggle.textContent = '☀️';
}

themeToggle.addEventListener('click', () => {
    body.classList.toggle('dark-mode');
    const isDark = body.classList.contains('dark-mode');
    localStorage.setItem('theme', isDark ? 'dark' : 'light');
    themeToggle.textContent = isDark ? '☀️' : '🌙';
});

document.getElementById('auth-form').addEventListener('submit', async function (e) {
    e.preventDefault();

    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value.trim();

    if (!username || !password) {
        alert("⚠️ Username və password boş ola bilməz.");
        return;
    }

    const payload = {
        username: username,
        password: password
    };

    try {
        const response = await fetch('auth/submit', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        const result = await response.json();

        if (response.ok) {
            alert(result.message || "✅ Success!");
            window.location.href = "/";
        } else {
            alert("❌ " + (result.error || "Unknown error"));
        }

    } catch (err) {
        console.error("⚠️ Network/Server error:", err);
        alert("❌ Server error. Zəhmət olmasa yenidən yoxlayın.");
    }
});

