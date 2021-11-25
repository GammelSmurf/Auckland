import axios from 'axios';
import authHeader from "./AuthHeader";

const API_SYNC_URL = 'http://localhost:8080/api/sync/';

const getTime = (id) => {
    return axios.get(API_SYNC_URL + id, { headers: authHeader() }).then(response => {
        return response
    });
}

export default {getTime}