import axios from "axios";
import authHeader from "./AuthHeader";


const API_URL = 'http://localhost:8080/api/admin/';

const getUsers = () => {
    return axios.get(API_URL, { headers: authHeader() });
}

const giveCash = (username, sum) => {
    return axios
        .post(API_URL + 'currency/add', {
            username: username,
            currency: sum
        }, {headers: authHeader()})
        .then(response => {

        });
}

const banUser = (username) => {
    return axios
        .post(API_URL + 'ban',
            {}
        , {headers: authHeader(), params: {username}})
        .then(response => {

        });
}

const unbanUser = (username) => {
    return axios
        .post(API_URL + 'unban',
            {}
            , {headers: authHeader(), params: {username}})
        .then(response => {

        });
}
export default {getUsers, giveCash, banUser, unbanUser}