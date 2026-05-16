// BudgetBee API Helper
const API = {
    BASE: '',

    getToken() {
        return localStorage.getItem('token') || sessionStorage.getItem('token') || null;
    },

    getHeaders() {
        const h = { 'Content-Type': 'application/json' };
        const token = this.getToken();
        if (token) h['Authorization'] = 'Bearer ' + token;
        return h;
    },

    isLoggedIn() {
        return !!this.getToken();
    },

    async get(path) {
        const res = await fetch(this.BASE + path, {
            method: 'GET',
            headers: this.getHeaders()
        });
        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.error || 'Request failed');
        }
        return res.json();
    },

    async post(path, data) {
        const res = await fetch(this.BASE + path, {
            method: 'POST',
            headers: this.getHeaders(),
            body: JSON.stringify(data)
        });
        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.error || 'Request failed');
        }
        return res.json();
    },

    async put(path, data) {
        const res = await fetch(this.BASE + path, {
            method: 'PUT',
            headers: this.getHeaders(),
            body: JSON.stringify(data)
        });
        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.error || 'Request failed');
        }
        return res.json();
    },

    async del(path) {
        const res = await fetch(this.BASE + path, {
            method: 'DELETE',
            headers: this.getHeaders()
        });
        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.error || 'Request failed');
        }
        return res.json();
    },

    async download(path, filename) {
        const res = await fetch(this.BASE + path, {
            headers: this.getHeaders()
        });
        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.error || 'Download failed');
        }
        const blob = await res.blob();
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        a.remove();
        URL.revokeObjectURL(url);
    }
};
