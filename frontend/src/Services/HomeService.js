import axios from 'axios';

const API_URL = 'http://localhost:8080/home/';
const getHello = () => {
    return axios.get(API_URL);
}

export default {getHello};