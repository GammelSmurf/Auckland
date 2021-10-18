import axios from 'axios';

const API_URL = '/api/home';
const getHello = () => {
    return axios.get(API_URL);
}

export default {getHello};