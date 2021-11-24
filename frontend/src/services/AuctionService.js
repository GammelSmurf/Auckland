import axios from 'axios';
import authHeader from "./AuthHeader";

const API_URL = 'http://localhost:8080/api/auctions/';
const API_URL_LOGS = 'http://localhost:8080/api/auction/logs/'
const getAllAuctions = () => {
    return axios.get(API_URL, { headers: authHeader() });
}

const getAuction = (id) => {
    return axios.get(API_URL + id, { headers: authHeader() }).then(response => {
        return response
    });
}

const createAuction = (values) =>{
    return axios
        .post(API_URL, {
            name: values.aucName,
            creatorUsername: values.username,
            description: values.aucDescription,
            beginDate: values.beginDate,
            lotDuration: values.lotDuration,
            boostTime: values.boostTime,
            usersLimit: values.usersLimit,
            status: values.status
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
    return axios
        .put(API_URL + values.aucId, {
            name: values.aucName,
            creatorUsername: values.username,
            description: values.aucDescription,
            beginDate: values.beginDate,
            lotDuration: values.lotDuration,
            boostTime: values.boostTime,
            usersLimit: values.usersLimit,
            status: values.status
        }, {headers: authHeader()})
        .then(response => {
            console.log("Update response:")
            console.log(response)
            return response
        });
};

const setStatusWaiting = (id) => {
    return axios
        .put(API_URL + id + "/available", {

        }, {headers: authHeader()})
        .then(response => {
            console.log("Change auctions status:")
            console.log(response)
            return response
        });
}

const getAuctionLogs = (auctionId) => {
    return axios.get(API_URL_LOGS + auctionId, { headers: authHeader() }).then(response => {
        return response
    });
}

export default {getAllAuctions, getAuction, deleteAuction, createAuction, updateAuction, setStatusWaiting, getAuctionLogs};