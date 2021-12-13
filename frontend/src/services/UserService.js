import axios from "axios";
import authHeader from "./AuthHeader";

const API_URL = 'http://localhost:8080/api/user/';

const getUserNotifications = (userId) => {
    return axios.get(API_URL + 'notifications/' + userId, { headers: authHeader() });
}
export default {getUserNotifications}