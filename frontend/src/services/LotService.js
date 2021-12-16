import axios from 'axios';
import authHeader from "./AuthHeader";

const API_URL = 'http://localhost:8080/api/lot/';

const getAllLots = () => {
    return axios.get(API_URL, { headers: authHeader() });
}

const getLotsByAuctionId = (id) => {
    return axios.get(API_URL + id, { headers: authHeader() });
}

const getWonTransferredLots = () => {
    return axios.get(API_URL + 'won/received', { headers: authHeader() });
}

const getWonNotTransferredLots = () => {
    return axios.get(API_URL + 'won/pending', { headers: authHeader() });
}

const acceptLot = (lotId) =>{
    return axios
        .post(API_URL + 'confirm/accept/' + lotId, {}, {headers: authHeader()})
        .then((response) => {
            return response;
        });
};

const transferLot = (lotId) =>{
    return axios
        .post(API_URL + 'confirm/transfer/' + lotId, {}, {headers: authHeader()})
        .then((response) => {
            return response;
        });
};

const createLot = (values) =>{
    return axios
        .post(API_URL, {
            name: values.name,
            description: values.description,
            minPrice: values.minPrice,
            priceIncreaseMinStep: values.priceIncreaseMinStep,
            pictureLink: values.pictureLink,
            auctionId: values.aucId

        }, {headers: authHeader()})
        .then((response) => {
            return response;
        });
};

const deleteLot = (id) => {
    return axios.delete(API_URL + id, {headers: authHeader()})
        .then((response)=>{
            console.log("Lot was deleted")
            console.log(response)
        })
}

const updateLot = (values) =>{
    console.log("LotService")
    console.log(values)
    return axios
        .put(API_URL + values.id, {
            name: values.name,
            description: values.description,
            minPrice: values.minPrice,
            priceIncreaseMinStep: values.priceIncreaseMinStep,
            pictureLink: values.pictureLink,
            auctionId: values.aucId
        }, {headers: authHeader()})
        .then(response => {
            console.log("Update lot response:")
            console.log(response)
            return response
        });
};

export default {createLot, updateLot, getLotsByAuctionId, deleteLot, getAllLots, getWonTransferredLots, getWonNotTransferredLots, acceptLot, transferLot}