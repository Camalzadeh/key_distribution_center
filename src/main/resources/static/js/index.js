document.getElementById('sidebarToggle').addEventListener('click', function () {
    const sidebar = document.getElementById('sidebar');
    sidebar.classList.toggle('collapsed');
});


const brandTitle = document.getElementById('brandTitle');

const themeToggleBtn = document.getElementById('themeToggle');
const body = document.body;

// On load: check and apply saved theme
const savedTheme = localStorage.getItem('theme');
if (savedTheme === 'dark') {
    body.classList.add('dark-mode');
    themeToggleBtn.textContent = '☀️';
    brandTitle.classList.remove('text-dark');
    brandTitle.classList.add('text-white');
}
// Toggle on button click
themeToggleBtn.addEventListener('click', () => {
    body.classList.toggle('dark-mode');
    const isDark = body.classList.contains('dark-mode');
    localStorage.setItem('theme', isDark ? 'dark' : 'light');
    themeToggleBtn.textContent = isDark ? '☀️' : '🌙';
    if (isDark) {
        brandTitle.classList.remove('text-dark');
        brandTitle.classList.add('text-white');
    } else {
        brandTitle.classList.remove('text-white');
        brandTitle.classList.add('text-dark');
    }
});

