import axios from 'axios';
import authHeader from "./AuthHeader";

const API_URL = 'http://localhost:8080/api/lot/';

const createLot = (values) =>{
    return axios
        .post(API_URL, {
            name: values.lotName,
            description: values.description,
            minBank: values.minBank,
            step: values.step,
            picture: values.picture,
            auctionId: values.auctionId

        }, {headers: authHeader()})
        .then(() => {

        });
};

export default {createLot}