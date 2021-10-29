import React, {useEffect, useState} from "react";
import AuctionService from "../services/AuctionService";

const Auction = (props) => {
    const [currentAuction, setCurrentAuction] = useState({});

    useEffect(() => {
        AuctionService.getAuction(props.match.params.id).then(
            (response) => {
                setCurrentAuction(response.data)
            }
        );
    }, []);


    return(
        <div className="container">
            <div className="wrapper">
                <h3>Auction page</h3>
                <p>{"Auction id: " + currentAuction.id}</p>
                <p>{"Auction name: " + currentAuction.name}</p>
            </div>
        </div>

    )
}

export default Auction;