import axios from 'axios';
import authHeader from "./AuthHeader";

const API_URL = 'http://localhost:8080/api/message/';

const getMessagesByAuctionId = (aucId) => {
    return axios.get(API_URL + aucId, { headers: authHeader() });
}

export default {getMessagesByAuctionId}