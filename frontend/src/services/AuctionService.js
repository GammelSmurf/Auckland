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
            description: values.aucDescription,
            beginDate: values.beginDate,
            lotDuration: values.lotDuration,
            boostTime: values.boostTime,
            usersLimit: values.usersLimit,
            userId: values.userId

        }, {headers: authHeader()})
        .then(response => {
            console.log("Create response:")
            console.log(response)
            return response
        });
};

const deleteAuction = (id) => {
    return axios.delete(API_URL + id, {headers: authHeader()})
        .then((response)=>{
            console.log("Auction was deleted")
            console.log(response)
        })
}

const updateAuction = (values) =>{
    console.log(values)
    return axios
        .put(API_URL + values.aucId, {
            name: values.aucName,
            description: values.aucDescription,
            beginDate: values.beginDate,
            lotDuration: values.lotDuration,
            boostTime: values.boostTime,
            usersLimit: values.usersLimit,
            userId: values.userId

        }, {headers: authHeader()})
        .then(response => {
            console.log("Update response:")
            console.log(response)
            return response
        });
};

export default {getAllAuctions, getAuction, deleteAuction, createAuction, updateAuction};