import axios from "axios";
import authHeader from "./AuthHeader";

const API_URL = 'http://localhost:8080/api/user/';

const getUserNotifications = () => {
    return axios.get(API_URL + 'notifications', { headers: authHeader() });
}
export default {getUserNotifications}