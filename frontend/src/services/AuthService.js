import axios from 'axios';

const API_URL = 'http://localhost:8080/api/auth/';

const login = (username, password) => {
    return axios
        .post(API_URL + 'signin', {
            username,
            password
        })
        .then(response => {
            if (response.data.token) {
                localStorage.setItem("user", JSON.stringify(response.data));
            }
            return response.data;
        });
};

const getCurrentUser = () => {
    return JSON.parse(localStorage.getItem("user"));
};

const isAuthenticated = () => {
    const currentUser = getCurrentUser();
    console.log(currentUser)
    return currentUser !== null;
};

const logout = () => {
    localStorage.removeItem("user");
};

export default {login, logout, getCurrentUser, isAuthenticated}