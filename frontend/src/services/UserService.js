import axios from "axios";
import authHeader from "./AuthHeader";

const API_URL = 'http://localhost:8080/api/user/';

const getUserNotifications = () => {
    return axios.get(API_URL + 'notifications', { headers: authHeader() });
}

const updateUser = (values) =>{
    return axios
        .put(API_URL + 'update', {
            username: values.username,
            firstName: values.firstName,
            secondName: values.secondName,
            about: values.about
        }, {headers: authHeader()})
        .then(response => {
            return response
        });
};

const getUser = (username) => {
    return axios.get(API_URL + username, { headers: authHeader() });
}

export default {getUserNotifications, updateUser, getUser}