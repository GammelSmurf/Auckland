import React, {useEffect, useState} from "react";
import AuctionService from "../services/AuctionService";
import {Col, Container, Row} from "react-bootstrap";

const Auction = (props) => {
    const [auction, setAuction] = useState({});

    useEffect(() => {
        AuctionService.getAuction(props.match.params.id).then(
            (response) => {
                setAuction(response.data)
            }
        );
    }, []);


    return(
        <Container>
            <Row>
                <h3>{auction.name}</h3>
            </Row>
            <Row>
                <Col>
                    <div>
                        <h5>Description</h5>
                        <p>{auction.description}</p>
                    </div>
                </Col>
                <Col>
                    2 of 3
                </Col>
                <Col>
                    3 of 3
                </Col>
            </Row>
        </Container>

    )
}

export default Auction;