import React, {useEffect, useState} from "react";
import AdminService from "../services/AdminService";
import {Col, Container, Row} from "react-bootstrap";
import LotService from "../services/LotService";


const Profile = (props) => {
    const [user, setUser] = useState({});
    //const [transLots, setTransLots] = useState([]);
    //const [notTransLots, setNotTransLots] = useState([]);

    //const errorPictureURL = 'https://st3.depositphotos.com/23594922/31822/v/600/depositphotos_318221368-stock-illustration-missing-picture-page-for-website.jpg';

    useEffect(() => {
        AdminService.getUsers().then(
            (response) => {
                setUser(response.data.filter(item =>
                    item.username === props.match.params.username)[0]);
            }
        );
        LotService.getWonTransferredLots().then((response)=>{console.log('Transfered',response.data);setTransLots(response.data)});
        LotService.getWonNotTransferredLots().then((response)=>{console.log('Not transfered',response.data);setNotTransLots(response.data)});
    }, []);

    /*const handleConfirmLot = (lotId) => {
        LotService.acceptLot(lotId).then((response)=>console.log(response.data));
    }*/

    return(
        <Container style={{paddingBottom: '80px'}}>
            <Row>
                <Col>
                    <div className="standardPageHeader">
                        <h2>Profile</h2>
                        <hr />
                    </div>
                    <h5>Username</h5>
                    <p>{user.username}</p>
                    <h5>Email</h5>
                    <p>{user.email}</p>
                    <h5>First name</h5>
                    <p>{user.firstName}</p>
                    <h5>Second name</h5>
                    <p>{user.secondName}</p>
                    <h5>About</h5>
                    <p>{user.about}</p>
                </Col>
            </Row>
           {/* <Row>
                <div className="standardPageHeader">
                    <h2>Not confirmed lots</h2>
                    <hr />
                </div>
                {notTransLots.filter(lot=>lot.winner.username === user.username).map(lot =>
                    <div key={lot.id} className="auctionBlock mt-4" style={{height: "300px"}}>
                        <div style={{width: "30%", display: "inline-block"}}>
                            <img alt="No image" src={lot.pictureLink ? lot.pictureLink : errorPictureURL}
                                 style={{width: "100%", maxHeight: "260px", objectFit: "cover"}}
                                 onError={(e)=>e.target.src=errorPictureURL}/>
                        </div>

                        <div style={{width: "70%", float: "right", padding: "20px"}}>
                            <h5>{lot.name}</h5>
                            <p>{lot.description}</p>
                            <div style={{textAlign: "right"}}>
                                {lot.buyerAcceptConfirmation ? <h5 className='basicAnim'>Waiting for creator transfer...</h5> :
                                    <Button variant="warning" style={{marginRight: "10px"}} onClick={()=>handleConfirmLot(lot.id)}>Confirm</Button>
                                }
                            </div>

                        </div>
                    </div>
                )}
            </Row>
            <Row>
                <div className="standardPageHeader">
                    <h2>Not transferred lots</h2>
                    <hr />
                </div>
                {transLots.filter(lot=>lot.winner.username !== user.username).map(lot =>
                    <div key={lot.id} className="auctionBlock mt-4" style={{height: "300px"}}>
                        <div style={{width: "30%", display: "inline-block"}}>
                            <img alt="No image" src={lot.pictureLink ? lot.pictureLink : errorPictureURL}
                                 style={{width: "100%", maxHeight: "260px", objectFit: "cover"}}
                                 onError={(e)=>e.target.src=errorPictureURL}/>
                        </div>

                        <div style={{width: "70%", float: "right", padding: "20px"}}>
                            <h5>{lot.name}</h5>
                            <p>{lot.description}</p>
                            <div style={{textAlign: "right"}}>
                                <Button variant="warning" style={{marginRight: "10px"}} onClick={()=>handleConfirmLot(lot.id)}>Confirm</Button>
                            </div>

                        </div>
                    </div>
                )}
            </Row>*/}
        </Container>
    )
}

export default Profile;