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
            name: values.name,
            creatorUsername: values.username,
            description: values.description,
            beginDateTime: values.beginDateTime,
            lotDurationTime: values.lotDurationTime,
            extraTime: values.extraTime,
            usersCountLimit: values.usersCountLimit,
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
        .put(API_URL + values.id, {
            name: values.name,
            creatorUsername: values.username,
            description: values.description,
            beginDateTime: values.beginDateTime,
            lotDurationTime: values.lotDurationTime,
            extraTime: values.extraTime,
            usersCountLimit: values.usersCountLimit,
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
            return response
        })
}

const getAuctionLogs = (auctionId) => {
    return axios.get(API_URL_LOGS + auctionId, {headers: authHeader()}).then(response => {
        return response
    });
}

const subscribe = (values) =>{
    return axios
        .post(API_URL + 'subscribe', {
            auctionId: values.auctionId,
            username: values.username
        }, {headers: authHeader()})
        .then(response => {
            return response
        });
};

const addCategory = (values) => {
    return axios
        .post(API_URL + 'category/add', {
            auctionId: values.auctionId,
            categoryId: values.categoryId
        }, {headers: authHeader()})
        .then(response => {
            return response
        });
}

const removeCategory = (values) => {
    return axios
        .post(API_URL + 'category/remove', {
            auctionId: values.auctionId,
            categoryId: values.categoryId
        }, {headers: authHeader()})
        .then(response => {
            return response
        });
}

const searchAuctions = (filterList, sortList) => {
    return axios
        .post(API_URL + 'search', {
            filterList: filterList,
            sortList: sortList
        }, {params:{page: 0, size: 10},headers: authHeader()})
        .then(response => {
            return response
        });
}

export default {getAllAuctions, getAuction, deleteAuction, createAuction, updateAuction, setStatusWaiting, getAuctionLogs, subscribe, addCategory, removeCategory, searchAuctions};