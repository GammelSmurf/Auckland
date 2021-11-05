import React, {useEffect, useRef, useState} from "react";
import {Col, Container, Row, Button, InputGroup, FormControl, Form} from "react-bootstrap";

import AuctionService from "../services/AuctionService";
import LotService from "../services/LotService";
import LotCarousel from "./LotCarousel";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import {faArrowLeft} from '@fortawesome/free-solid-svg-icons'
import EditAucForm from "./EditAucForm";
import AuthService from "../services/AuthService";
import EditLotModal from "./EditLotModal";
import ModalDialog from "./ModalDialog";

const Auction = (props) => {
    const [validated, setValidated] = useState(false);
    const [auction, setAuction] = useState({});
    const [lots, setLots] = useState([]);
    const [onEdit, setOnEdit] = useState(false);
    const [aucDateTime, setAucDateTime] = useState("");
    const currentUser = AuthService.getCurrentUser();
    const [values, setValues] = useState({});
    const [isLotModalActive, setIsLotModalActive] = useState(false);
    const [currentLot, setCurrentLot] = useState({});
    const [isModalDeleteAuction, setIsModalDeleteAuction] = useState(false);
    const [isModalDeleteLot, setIsModalDeleteLot] = useState(false);

    const lotSection = useRef(null);

    useEffect(() => {
        AuctionService.getAuction(props.match.params.id).then(
            (response) => {
                setAuction(response.data)
                setAucDateTime(parseDisplayDate(response.data.beginDate))
                setValues({
                    userId: currentUser.id,
                    aucId: response.data.id,
                    aucName: response.data.name,
                    aucDescription: response.data.description,
                    usersLimit: response.data.usersLimit,
                    beginDate: response.data.beginDate,
                    lotDuration: response.data.lotDuration,
                    boostTime: response.data.boostTime
                });
                LotService.getLotsByAuctionId(response.data.id).then(
                    (response) => {
                        let dataPrev = [];
                        response.data.forEach(item => {
                            dataPrev.push(
                                item
                            )
                        })
                        setLots(dataPrev);
                    }
                );
            }
        );
    }, []);

    const setDefaultLotValues = (currentLot) => {
        setValues({...values,
            lotId: currentLot.id,
            lotName: currentLot.name,
            lotDescription: currentLot.description,
            minBank: currentLot.minBank,
            step: currentLot.step,
            picture: currentLot.picture
        })
    }

    const handleLotModalClose = () => {
        setIsLotModalActive(false);
    }

    const handleDeleteAuction = () => {
        AuctionService.deleteAuction(auction.id).then(()=>props.history.push("/auctions"));
    }

    const handleLotSubmit = () => {

        if(currentLot.id){
            LotService.updateLot(values).then(() => {handleLotModalClose(); window.location.reload()});
        }
        else{
            console.log(values)
            LotService.createLot(values).then(() => {handleLotModalClose(); window.location.reload()});
        }
    }

    const handleBack = () => {
        props.history.push("/auctions")
    }

    const handleEdit = () => {
        setOnEdit(true);
    }

    const handleDeleteLot = (id) => {
        LotService.deleteLot(id).then(()=>{window.location.reload();})
    }

    const parseDisplayDate = (date) => {
        const dateTime = new Date(date);
        const options = {month: 'long', day: 'numeric',
            hour: '2-digit', minute: '2-digit', second: '2-digit'};
        const dateTimeFormat = new Intl.DateTimeFormat('ru-RU', options).format;
        return dateTimeFormat(dateTime);
    }

    const handleChange = (name) => (e) => {
        setValues({ ...values, [name]: e.target.value });
    };

    const handleSubmit = (event) => {
        const form = event.currentTarget;
        if (form.checkValidity() === false) {
            setValidated(true);
        }
        else{
            AuctionService.updateAuction(values).then(() => window.location.reload());
            setOnEdit(false);
        }
        event.preventDefault();
        event.stopPropagation();
    }

    return(
        <Container>
            <div className="wrapper">
                <Form noValidate validated={validated} onSubmit={handleSubmit}>
                    <div style={{height: "100%"}}>
                        <Row>
                            <Col xs={12} md={4}>
                                <div>
                                    {onEdit ?
                                        <Form.Group>
                                            <InputGroup>
                                                <InputGroup.Text>Name</InputGroup.Text>
                                                <FormControl required defaultValue={auction.name} onChange={handleChange("aucName")}/>
                                            </InputGroup>
                                            <Form.Control.Feedback type="invalid">
                                                The value cannot be empty
                                            </Form.Control.Feedback>
                                        </Form.Group>
                                        :
                                        <h3><FontAwesomeIcon icon={faArrowLeft} color="#FFCA2C" size="sm" onClick={handleBack} style={{cursor: "hand"}}/> {auction.name}</h3>}
                                </div>
                            </Col>
                            <Col xs={12} md={4}>
                                <div style={{textAlign: "center"}}>
                                    <h5><span style={{borderBottom: "2px solid #9434B3"}}>{auction.status}</span></h5>
                                </div>
                            </Col>
                            <Col xs={12} md={4}>
                                <div style={{textAlign: "right"}}>
                                    <Button variant="warning" style={{marginRight: "10px"}} onClick={()=>lotSection.current.scrollIntoView()}>See lots</Button>
                                    {onEdit ?
                                        <Button type="submit" >Save</Button>
                                        :
                                        <>
                                            <Button variant="warning" onClick={handleEdit} style={{marginRight: "10px"}}>Edit</Button>
                                            <Button variant="secondary" onClick={() => setIsModalDeleteAuction(true)}>Delete</Button>
                                            <Button type="submit" style={{display: "none"}} />
                                        </>
                                    }
                                </div>
                            </Col>
                        </Row>
                        <Row className="mt-2">
                            <Col xs={12} md={4}>
                                <div className="auctionBlock" style={{height: "300px"}}>
                                    <h5>Description</h5>
                                    {onEdit ?
                                        <Form.Group>
                                            <FormControl as="textarea" rows={9} defaultValue={auction.description} onChange={handleChange("aucDescription")} style={{resize: "none"}}/>
                                        </Form.Group> :
                                        <p>{auction.description}</p>}
                                </div>
                                <div className="auctionBlock mt-3" style={{height: "300px"}}>
                                    <h5>Logger</h5>
                                    <div>
                                        <p>User повысил ставку до 1800 </p>
                                        <p>User повысил ставку до 1800</p>
                                        <p>User повысил ставку до 1800</p>
                                        <p>User повысил ставку до 1800</p>
                                        <p>User повысил ставку до 1800</p>
                                    </div>
                                </div>
                            </Col>
                            <Col xs={12} md={4}>
                                <div style={{textAlign: "center"}}>
                                    <h5>Auction starts in</h5>
                                    {onEdit ? <EditAucForm auction={auction} handleChange={handleChange}/>
                                        :
                                        <h1>{aucDateTime}</h1>}
                                    <div className="mt-1">
                                        <LotCarousel lots={lots}/>
                                    </div>
                                    <div className="mt-1">
                                        {!onEdit && <Button variant="warning" style={{marginTop: "20px", padding: "10px 30px"}}>
                                            Start countdown
                                        </Button>}
                                    </div>
                                    {/*<div style={{marginTop: "10px"}}>
                                <h5>Current price</h5>
                                <h1>10000 $</h1>
                                <InputGroup>
                                    <InputGroup.Text>$</InputGroup.Text>
                                    <FormControl aria-label="Amount (to the nearest dollar)"
                                                 placeholder="Enter the amount"/>
                                    <InputGroup.Text>.00</InputGroup.Text>
                                </InputGroup>
                                <Button variant="warning" style={{marginTop: "20px", padding: "10px 30px"}}>
                                    Make a bet
                                </Button>
                            </div>*/}
                                </div>
                            </Col>
                            <Col xs={12} md={4}>
                                <div>
                                    <div className="auctionBlock" style={{height: "618px"}}>
                                        <h5>Чат</h5>
                                    </div>
                                </div>
                            </Col>
                        </Row>
                    </div>
                    <Row>
                        <div ref={lotSection} style={{paddingTop:"100px"}}>
                            <div>
                                <div style={{width: "50%", textAlign: "left", display: "inline-block"}}>
                                    <h3>Lots</h3>
                                </div>
                                <div style={{width: "50%", textAlign: "right", display: "inline-block"}}>
                                    <Button variant="warning" onClick={() => {setIsLotModalActive(true); setCurrentLot({})}}>Add lot</Button>
                                </div>
                            </div>
                            {lots.map(lot =>
                                <div key={lot.id} className="auctionBlock mt-4" style={{height: "300px"}}>
                                    <div style={{width: "30%", display: "inline-block"}}>
                                        <img src={lot.picture} style={{width: "100%", maxHeight: "260px"}}/>
                                    </div>

                                    <div style={{width: "70%", float: "right", padding: "20px"}}>
                                        <h5>{lot.name}</h5>
                                        <p>{lot.description}</p>
                                        <div style={{textAlign: "right"}}>
                                        <Button variant="warning" style={{marginRight: "10px"}} onClick={() => {setCurrentLot(lot); setDefaultLotValues(lot); setIsLotModalActive(true)}}>Edit</Button>
                                        <Button variant="secondary" onClick={()=>{setCurrentLot(lot);setIsModalDeleteLot(true)}}>Delete</Button>
                                    </div>

                                    </div>
                                </div>
                            )}
                        </div>
                    </Row>
                </Form>
                <EditLotModal show={isLotModalActive} hide={handleLotModalClose} lot={currentLot} handleChange={handleChange} handleLotSubmit={handleLotSubmit}/>
            </div>
            <ModalDialog show={isModalDeleteAuction} hide={() => setIsModalDeleteAuction(false)} title={"Delete auction"} body={"Do you really want to delete this auction"} action={handleDeleteAuction}/>
            <ModalDialog show={isModalDeleteLot} hide={() => setIsModalDeleteLot(false)} title={"Delete lot"} body={"Do you really want to delete lot " + currentLot.name + " ?"} action={() => handleDeleteLot(currentLot.id)}/>
        </Container>

    )
}

export default Auction;