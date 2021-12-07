import axios from 'axios';
import authHeader from "./AuthHeader";

const API_URL = 'http://localhost:8080/api/tag/';

const getTagsByAuctionId = (aucId) => {
    return axios.get(API_URL + aucId, { headers: authHeader() });
}

const addTag = (values) =>{
    return axios
        .post(API_URL, {
            name: values.name,
            categoryId: values.categoryId,
            auctionId: values.auctionId
        }, {headers: authHeader()})
        .then((response) => {
            return response;
        });
};

const deleteTag = (tagId) => {
    return axios.delete(API_URL + tagId, {headers: authHeader()})
        .then((response)=>{
            return response;
        })
}

export default {getTagsByAuctionId, addTag, deleteTag}