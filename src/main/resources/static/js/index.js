document.getElementById('sidebarToggle').addEventListener('click', function () {
    const sidebar = document.getElementById('sidebar');
    sidebar.classList.toggle('collapsed');
});

const themeToggleBtn = document.getElementById('themeToggle');
const body = document.body;

// Function to apply theme styles
function applyTheme(isDark) {
    if (isDark) {
        body.classList.add('dark-mode');
        themeToggleBtn.textContent = '☀️';
    } else {
        body.classList.remove('dark-mode');
        themeToggleBtn.textContent = '🌙';
    }
}

// Initial Load
const savedTheme = localStorage.getItem('theme');
applyTheme(savedTheme === 'dark');

// Click Event
themeToggleBtn.addEventListener('click', () => {
    const isDark = !body.classList.contains('dark-mode');
    applyTheme(isDark);
    localStorage.setItem('theme', isDark ? 'dark' : 'light');
});

