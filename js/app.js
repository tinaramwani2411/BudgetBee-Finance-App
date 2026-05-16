// BudgetBee Main Application

document.addEventListener('DOMContentLoaded', function() {
    setupExpenseDate();
    initApp();
});

async function initApp() {
    loadDashboard();

    const user = await checkAuth();
    if (user) {
        updateAuthUI(user);
    }

    // Set up report month to current
    const now = new Date();
    document.getElementById('reportMonth').value = now.toISOString().slice(0, 7);

    // Theme
    initTheme();
}

function showSection(section) {
    document.querySelectorAll('.section').forEach(s => {
        s.style.display = 'none';
    });
    document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));

    const sectionMap = {
        'dashboard': 'dashboardSection',
        'expenses': 'dashboardSection',
        'analytics': 'analyticsSection',
        'reports': 'reportsSection',
        'profile': 'profileSection'
    };

    const secId = sectionMap[section] || 'dashboardSection';
    document.getElementById(secId).style.display = 'block';

    const link = document.querySelector(`.nav-link[data-section="${section}"]`);
    if (link) link.classList.add('active');

    if (section === 'dashboard' || section === 'expenses') {
        loadDashboard();
    }
    if (section === 'analytics') {
        loadDashboard();
    }
    if (section === 'profile') {
        loadProfile();
    }

    // Close mobile nav
    document.getElementById('navLinks').classList.remove('open');
}

function toggleNav() {
    document.getElementById('navLinks').classList.toggle('open');
}

// Theme
function initTheme() {
    const saved = localStorage.getItem('budgetbee-theme');
    if (saved === 'dark') {
        document.documentElement.setAttribute('data-theme', 'dark');
        document.querySelector('.theme-toggle').textContent = '☀️';
    }
}

function toggleTheme() {
    const html = document.documentElement;
    const btn = document.querySelector('.theme-toggle');
    if (html.getAttribute('data-theme') === 'dark') {
        html.removeAttribute('data-theme');
        localStorage.setItem('budgetbee-theme', 'light');
        btn.textContent = '🌙';
    } else {
        html.setAttribute('data-theme', 'dark');
        localStorage.setItem('budgetbee-theme', 'dark');
        btn.textContent = '☀️';
    }
}

// Profile
async function loadProfile() {
    if (!API.isLoggedIn()) {
        showToast('Please login to view profile', 'error');
        return;
    }
    try {
        const user = await API.get('/api/auth/me');
        document.getElementById('profUsername').value = user.username;
        document.getElementById('profFullName').value = user.fullName || '';
        document.getElementById('profEmail').value = user.email || '';
    } catch (err) {
        showToast('Error loading profile', 'error');
    }
}

async function handleUpdateProfile(e) {
    e.preventDefault();
    try {
        const data = await API.put('/api/users/profile', {
            fullName: document.getElementById('profFullName').value,
            email: document.getElementById('profEmail').value
        });
        showToast(data.message || 'Profile updated!', 'success');
        const greeting = document.getElementById('userGreeting');
        greeting.textContent = data.fullName || data.username;
    } catch (err) {
        showToast(err.message, 'error');
    }
}
