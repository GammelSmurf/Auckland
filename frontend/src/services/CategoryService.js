import axios from 'axios';
import authHeader from "./AuthHeader";

const API_URL = 'http://localhost:8080/api/category/';

const getAllCategories = () => {
    return axios.get(API_URL, { headers: authHeader() });
}

export default {getAllCategories}