import axios from 'axios';
import authHeader from "./AuthHeader";

const API_URL = 'http://localhost:8080/api/auctions/';
const getAllAuctions = () => {
    return axios.get(API_URL, { headers: authHeader() });
}

const getAuction = (id) => {
    return axios.get(API_URL + id, { headers: authHeader() }).then(response => {
        console.log("Auc response:")
        console.log(response)
        return response
    });
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
            console.log("Auc response:")
            console.log(response)
            return response
        });
};

export default {getAllAuctions, getAuction, createAuction};