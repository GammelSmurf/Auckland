import axios from 'axios';
import authHeader from "./AuthHeader";

const API_URL = 'http://localhost:8080/api/auction/';
const getAllAuctions = () => {
    return axios.get(API_URL, { headers: authHeader() });
}

const createAuction = (values) =>{
    console.log(values)
    return axios
        .post(API_URL, {
            name: values.aucName,
            beginDate: values.beginDate,
            lotDuration: values.lotDuration,
            boostTime: values.boostTime,
            usersLimit: values.usersLimit,
            userId: values.userId

        }, {headers: authHeader()})
        .then(response => {
            return response
        });
};

export default {getAllAuctions, createAuction};