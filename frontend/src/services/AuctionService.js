import axios from 'axios';
import authHeader from "./AuthHeader";

const API_URL = 'http://localhost:8080/api/auction/';
const getAllAuctions = () => {
    return axios.get(API_URL, { headers: authHeader() });
}

export default {getAllAuctions};