// BudgetBee Authentication Module

async function handleLogin(e) {
    e.preventDefault();
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;
    const remember = document.getElementById('rememberMe').checked;

    try {
        const data = await API.post('/api/auth/login', { username, password });

        if (remember) {
            localStorage.setItem('token', data.token);
        } else {
            sessionStorage.setItem('token', data.token);
        }

        closeModal('loginModal');
        showToast('Welcome back, ' + data.username + '!', 'success');
        updateAuthUI(data);
        loadDashboard();
        document.getElementById('loginForm').reset();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function handleRegister(e) {
    e.preventDefault();
    const username = document.getElementById('regUsername').value;
    const email = document.getElementById('regEmail').value;
    const fullName = document.getElementById('regFullName').value;
    const password = document.getElementById('regPassword').value;
    const confirm = document.getElementById('regConfirmPassword').value;

    if (password !== confirm) {
        showToast('Passwords do not match', 'error');
        return;
    }

    try {
        const data = await API.post('/api/auth/register', {
            username, email, fullName, password
        });

        localStorage.setItem('token', data.token);
        closeModal('registerModal');
        showToast('Account created! Welcome, ' + data.username + '!', 'success');
        updateAuthUI(data);
        loadDashboard();
        document.getElementById('registerForm').reset();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

function handleLogout() {
    localStorage.removeItem('token');
    sessionStorage.removeItem('token');
    showToast('Logged out successfully', 'info');
    updateAuthUI(null);
    showSection('dashboard');
    location.reload();
}

async function checkAuth() {
    if (!API.isLoggedIn()) return null;
    try {
        const data = await API.get('/api/auth/me');
        return data;
    } catch (err) {
        localStorage.removeItem('token');
        sessionStorage.removeItem('token');
        return null;
    }
}

function updateAuthUI(user) {
    const loginBtn = document.getElementById('loginBtn');
    const registerBtn = document.getElementById('registerBtn');
    const userMenu = document.getElementById('userMenu');
    const greeting = document.getElementById('userGreeting');

    if (user) {
        loginBtn.style.display = 'none';
        registerBtn.style.display = 'none';
        userMenu.style.display = 'block';
        greeting.textContent = user.fullName || user.username;
    } else {
        loginBtn.style.display = 'inline-flex';
        registerBtn.style.display = 'inline-flex';
        userMenu.style.display = 'none';
    }
}

function openModal(id) {
    document.getElementById(id).classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeModal(id) {
    document.getElementById(id).classList.remove('active');
    document.body.style.overflow = '';
}

function switchModal(id) {
    document.querySelectorAll('.modal').forEach(m => m.classList.remove('active'));
    document.getElementById(id).classList.add('active');
}

// Close modals on outside click
document.addEventListener('click', function(e) {
    document.querySelectorAll('.modal').forEach(m => {
        if (e.target === m) {
            m.classList.remove('active');
            document.body.style.overflow = '';
        }
    });
});

// Close modals on Escape
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        document.querySelectorAll('.modal').forEach(m => {
            m.classList.remove('active');
            document.body.style.overflow = '';
        });
    }
});
