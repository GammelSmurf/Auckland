import axios from 'axios';
import authHeader from "./AuthHeader";

const API_URL = 'http://localhost:8080/api/lot/';

const getAllLots = () => {
    return axios.get(API_URL, { headers: authHeader() });
}

const getLotsByAuctionId = (id) => {
    return axios.get(API_URL + id, { headers: authHeader() });
}

const createLot = (values) =>{
    return axios
        .post(API_URL, {
            name: values.name,
            description: values.description,
            minBank: values.minBank,
            step: values.step,
            picture: values.picture,
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
            minBank: values.minBank,
            step: values.step,
            picture: values.picture,
            auctionId: values.aucId
        }, {headers: authHeader()})
        .then(response => {
            console.log("Update lot response:")
            console.log(response)
            return response
        });
};

export default {createLot, updateLot, getLotsByAuctionId, deleteLot, getAllLots}