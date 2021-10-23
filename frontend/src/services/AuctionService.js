import axios from 'axios';
import authHeader from "./AuthHeader";

const API_URL = 'http://localhost:8080/api/auction/';
const getAllAuctions = () => {
    return axios.get(API_URL, { headers: authHeader() });
}

const createAuction = (newAuction) =>{
    console.log(newAuction)
    return axios
        .post(API_URL, {
            name: newAuction.name,
            beginDate: newAuction.beginDate,
            lotDuration: newAuction.lotDuration,
            boostTime: newAuction.boostTime,
            usersLimit: newAuction.usersLimit,
            userId: newAuction.userId

        }, {headers: authHeader()})
        .then(response => {

        });
};

export default {getAllAuctions, createAuction};