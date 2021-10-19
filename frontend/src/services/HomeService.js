import axios from 'axios';
import authHeader from "./AuthHeader";

const API_URL = 'http://localhost:8080/home';
const getHello = () => {
    return axios.get(API_URL, { headers: authHeader() });
}

export default {getHello};