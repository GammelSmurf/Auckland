import React, {useEffect, useRef, useState} from "react";
import {Col, Container, Row, Button, InputGroup, FormControl, Form, Spinner} from "react-bootstrap";

import AuctionService from "../services/AuctionService";
import LotService from "../services/LotService";
import LotCarousel from "./LotCarousel";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import {faArrowLeft} from '@fortawesome/free-solid-svg-icons'
import EditAucForm from "./EditAucForm";
import AuthService from "../services/AuthService";
import EditLotModal from "./EditLotModal";
import ModalDialog from "./ModalDialog";
import BetService from "../services/BetService";

const Auction = (props) => {
    const [validated, setValidated] = useState(false);
    const [auction, setAuction] = useState({});
    const [lots, setLots] = useState([]);
    const [onEdit, setOnEdit] = useState(false);
    const [strTimer, setStrTimer] = useState(null);
    const currentUser = AuthService.getCurrentUser();
    const [aucValues, setAucValues] = useState({});
    const [lotValues, setLotValues] = useState({});
    const [isLotModalActive, setIsLotModalActive] = useState(false);
    const [currentLot, setCurrentLot] = useState({});
    const [isModalDeleteAuction, setIsModalDeleteAuction] = useState(false);
    const [isModalDeleteLot, setIsModalDeleteLot] = useState(false);
    const [isModalStartCountDown, setIsModalStartCountDown] = useState(false);
    const [status, setStatus] = useState('');
    const [logs, setLogs] = useState([]);
    const [finishTime, setFinishTime] = useState('');
    const [currentPrice, setCurrentPrice] = useState('');
    const [bet, setBet] = useState();
    const [client, setClient] = useState(null);

    const lotSection = useRef(null);

    const addZeroBefore = (n) => {
        return (n < 10 ? '0' : '') + n;
    }

    const formatDate = (date) => {
        return `${date.getFullYear()}-${addZeroBefore(date.getMonth() + 1)}-${addZeroBefore(date.getDate())}T${addZeroBefore(date.getHours())}:${addZeroBefore(date.getMinutes())}:${addZeroBefore(date.getSeconds())}`;
    }

    const parseDateToDisplay = (inputDate) => {
        const date = new Date(inputDate);
        const options = {
            month: 'long', day: 'numeric',
            hour: '2-digit', minute: '2-digit', second: '2-digit'
        };
        const dateTimeFormat = new Intl.DateTimeFormat('ru-RU', options).format;
        return dateTimeFormat(date);
    }

    const parseAuctionDate = (inputDate) => {
        return formatDate(new Date(inputDate));
    }

    const parseResponseDate = (inputDate) => {
        return formatDate(new Date(inputDate)).split('T').join(' ');
    }

    const onBetChange = (e) => {
        setBet(e.target.value);
    }

    useEffect(() => {
        let timer;
        AuctionService.getAuction(props.match.params.id).then(
            (response) => {
                setStatus(response.data.status);
                switch (response.data.status) {
                    case 'DRAFT':
                        setStrTimer(parseDateToDisplay(response.data.beginDate));
                        break;
                    case 'WAITING':
                        BetService.getTime(response.data.id).then(
                            (syncResponse) => timer = activateTimer(syncResponse.data.timeUntil, response.data.id)
                        );
                        break;
                    case 'RUNNING':
                        BetService.getTime(response.data.id).then(
                            (response) => timer = activateTimer(response.data.timeUntil, response.data.id)
                        );
                        break;
                    case 'FINISHED':
                        break;
                }
                setAuction({...response.data, beginDate: parseAuctionDate(response.data.beginDate)});
                setAucValues({
                    username: currentUser.username,
                    id: response.data.id,
                    name: response.data.name,
                    description: response.data.description,
                    usersLimit: response.data.usersLimit,
                    beginDate: parseResponseDate(response.data.beginDate),
                    lotDuration: response.data.lotDuration,
                    boostTime: response.data.boostTime,
                    status: response.data.status
                });
                LotService.getLotsByAuctionId(response.data.id).then(
                    (response) => {
                        setLots(response.data);
                        response.data.length > 0 && setFinishTime(parseDateToDisplay(response.data.at(-1).endTime));
                    }
                );
                AuctionService.getAuctionLogs(response.data.id).then(
                    response => {
                        setLogs(response.data);
                        localStorage.setItem("logs", JSON.stringify(response.data));
                    }
                )
            }
        );
        return () => clearInterval(timer);
    }, []);

    const activateTimer = (inputSeconds, auctionId) => {
        let time = inputSeconds;
        const timer = setInterval(function () {
            const hours = time / 3600 % 60;
            const minutes = time / 60 % 60;
            const seconds = time % 60;
            if (time <= 0) {
                clearInterval(timer);
                BetService.getTime(auctionId).then(
                    (response) => {
                        console.log('Sync')
                        console.log(response)
                        if(status !== response.data.auctionStatus){
                            setStatus(response.data.auctionStatus);
                        }
                        if(response.data.auctionStatus !== 'FINISHED'){
                            activateTimer(response.data.timeUntil, auctionId);
                        }
                    }
                );
            } else {
                setStrTimer(`${addZeroBefore(Math.trunc(hours))}:${addZeroBefore(Math.trunc(minutes))}:${addZeroBefore(seconds)}`);
            }
            --time;
        }, 1000);
        return timer;
    }

    const handleStartCountdown = () => {
        AuctionService.setStatusWaiting(auction.id).then(() => window.location.reload());
    }

    const setDefaultLotValues = (currentLot) => {
        setLotValues({
            id: currentLot.id,
            name: currentLot.name,
            description: currentLot.description,
            minBank: currentLot.minBank,
            step: currentLot.step,
            picture: currentLot.picture,
            aucId: auction.id
        })
    }

    const handleDeleteAuction = () => {
        AuctionService.deleteAuction(auction.id).then(() => props.history.push("/auctions"));
    }

    const handleLotSubmit = () => {
        if (currentLot.id) {
            LotService.updateLot(lotValues).then(() => {
                setLots(lots.filter(lot=>lot.id !== currentLot.id).concat(lotValues));
                setIsLotModalActive(false);
            });
        } else {
            LotService.createLot(lotValues).then((lot) => {
                console.log(lot)
                setLots([...lots, {...lotValues, id: lot.data.id}]);
                setIsLotModalActive(false);
            });
        }
    }

    const handleBack = () => {
        props.history.push("/auctions")
    }

    const handleDeleteLot = (id) => {
        LotService.deleteLot(id).then(() => {
            setLots(lots.filter(lot=>lot.id !== id));
            setIsModalDeleteLot(false);
        })
    }

    const handleChangeAuction = (name) => (e) => {
        let value = e.target.value;
        setAuction({...auction, [name]: value})
        if (name === "beginDate") {
            setStrTimer(parseDateToDisplay(value));
            value = parseResponseDate(value);
        }
        setAucValues({...aucValues, [name]: value});
    };

    const handleChangeLot = (name) => (e) => {
        setLotValues({...lotValues, [name]: e.target.value})
    }

    const handleSubmit = (event) => {
        const form = event.currentTarget;
        if (form.checkValidity() === false) {
            setValidated(true);
        } else {
            AuctionService.updateAuction(aucValues).then(() => setOnEdit(false));
        }
        event.preventDefault();
        event.stopPropagation();
    }

    return (
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
                                                <FormControl required defaultValue={auction.name}
                                                             onChange={handleChangeAuction("name")}/>
                                            </InputGroup>
                                            <Form.Control.Feedback type="invalid">
                                                The value cannot be empty
                                            </Form.Control.Feedback>
                                        </Form.Group>
                                        :
                                        <h3><FontAwesomeIcon icon={faArrowLeft} color="#FFCA2C" size="sm"
                                                             onClick={handleBack}
                                                             style={{cursor: "hand"}}/> {auction.name}</h3>}
                                </div>
                            </Col>
                            <Col xs={12} md={4}>
                                <div style={{textAlign: "center"}}>
                                    <h5><span style={{borderBottom: "2px solid #9434B3"}}>{status}</span>
                                    </h5>
                                </div>
                            </Col>
                            <Col xs={12} md={4}>
                                <div style={{textAlign: "right"}}>
                                    <Button variant="warning" style={{marginRight: "10px"}}
                                            onClick={() => lotSection.current.scrollIntoView()}>See lots</Button>
                                    {status === 'DRAFT' && (onEdit ?
                                            <Button type="submit">Save</Button>
                                            :
                                            <>
                                                <Button variant="warning" onClick={()=>setOnEdit(true)}
                                                        style={{marginRight: "10px"}}>Edit</Button>
                                                <Button variant="secondary"
                                                        onClick={() => setIsModalDeleteAuction(true)}>Delete</Button>
                                                <Button type="submit" style={{display: "none"}}/>
                                            </>
                                    )}
                                </div>
                            </Col>
                        </Row>
                        <Row className="mt-2">
                            <Col xs={12} md={4}>
                                <div className="auctionBlock" style={{height: "300px"}}>
                                    <h5>Description</h5>
                                    {onEdit ?
                                        <Form.Group>
                                            <FormControl as="textarea" rows={9} defaultValue={auction.description}
                                                         onChange={handleChangeAuction("description")}
                                                         style={{resize: "none"}}/>
                                        </Form.Group> :
                                        <p>{auction.description}</p>}
                                </div>
                                <div className="auctionBlock mt-3" style={{height: "300px"}}>
                                    <h5>Logger</h5>
                                    <div>
                                        {logs.map(log =>
                                            <p key={log.id}>{log.logMessage} <span><b>({log.logTime})</b></span></p>
                                        )}
                                    </div>
                                </div>
                            </Col>
                            <Col xs={12} md={4}>
                                <div style={{textAlign: "center", overflow: 'hidden'}}>
                                    {(status === 'DRAFT' || status === 'WAITING') &&
                                        <>
                                            <h5 className='basicAnim'>Auction starts in</h5>
                                            {onEdit ? <EditAucForm auction={auction} handleChange={handleChangeAuction}/>
                                                :
                                                <>
                                                    {strTimer ? <h1>{strTimer}</h1> :
                                                        <Spinner animation="border" role="status"/>}
                                                </>}
                                        </>
                                    }
                                    {status === 'RUNNING' &&
                                        <>
                                            <h5 className='basicAnim'>Lot is over in</h5>
                                            {strTimer ? <h1>{strTimer}</h1> :
                                                <Spinner animation="border" role="status"/>}
                                        </>
                                    }
                                    {status === 'FINISHED' &&
                                        <>
                                            <h5 className='basicAnim'>Auction ended in</h5>
                                            <h1>{finishTime}</h1>
                                        </>
                                    }
                                    <div className="mt-1">
                                        <LotCarousel lots={lots}/>
                                    </div>
                                    <div className="mt-1">
                                        {status === 'DRAFT' && (!onEdit &&
                                            <Button variant="warning"
                                                    style={{marginTop: "20px", padding: "10px 30px"}}
                                                    onClick={() => setIsModalStartCountDown(true)}>
                                                Start countdown
                                            </Button>)}
                                    </div>
                                    {status === 'RUNNING' &&
                                    <div className='basicAnim' style={{marginTop: "10px"}}>
                                        <h5>Current price</h5>
                                        <h1>{currentPrice}$</h1>
                                        <InputGroup>
                                            <InputGroup.Text>$</InputGroup.Text>
                                            <FormControl aria-label="Amount (to the nearest dollar)"
                                                         placeholder="Enter the amount" onChange={onBetChange}/>
                                            <InputGroup.Text>.00</InputGroup.Text>
                                        </InputGroup>
                                        <Button onClick={() => SocketService.sendBet(auction.id, currentUser.username, bet)} variant="warning" style={{marginTop: "20px", padding: "10px 30px"}}>
                                            Make a bet
                                        </Button>
                                    </div>}
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
                        <div ref={lotSection} style={{paddingTop: "100px"}}>
                            <div>
                                <div style={{width: "50%", textAlign: "left", display: "inline-block"}}>
                                    <h3>Lots</h3>
                                </div>
                                <div style={{width: "50%", textAlign: "right", display: "inline-block"}}>
                                    {status === 'DRAFT' &&
                                    <Button variant="warning" onClick={() => {
                                        setIsLotModalActive(true);
                                        setCurrentLot({})
                                        setDefaultLotValues({})
                                        setIsLotModalActive(true);
                                    }}>Add lot</Button>
                                    }
                                </div>
                            </div>
                            {lots.map(lot =>
                                <div key={lot.id} className="auctionBlock mt-4" style={{height: "300px"}}>
                                    <div style={{width: "30%", display: "inline-block"}}>
                                        <img alt="No image" src={lot.picture}
                                             style={{width: "100%", maxHeight: "260px", objectFit: "cover"}}
                                        onError={(e)=>e.target.src='https://st3.depositphotos.com/23594922/31822/v/600/depositphotos_318221368-stock-illustration-missing-picture-page-for-website.jpg'}/>
                                    </div>

                                    <div style={{width: "70%", float: "right", padding: "20px"}}>
                                        <h5>{lot.name}</h5>
                                        <p>{lot.description}</p>
                                        <div style={{textAlign: "right"}}>
                                            {status === 'DRAFT' &&
                                            <>
                                                <Button variant="warning" style={{marginRight: "10px"}}
                                                        onClick={() => {
                                                            setCurrentLot(lot);
                                                            setDefaultLotValues(lot);
                                                            setIsLotModalActive(true)
                                                        }}>Edit</Button>
                                                <Button variant="secondary" onClick={() => {
                                                    setCurrentLot(lot);
                                                    setIsModalDeleteLot(true)
                                                }}>Delete</Button>
                                            </>
                                            }
                                        </div>

                                    </div>
                                </div>
                            )}
                        </div>
                    </Row>
                </Form>
                <EditLotModal show={isLotModalActive} hide={()=>setIsLotModalActive(false)} lot={currentLot}
                              handleChange={handleChangeLot} handleLotSubmit={handleLotSubmit}/>
            </div>
            <ModalDialog show={isModalDeleteAuction} hide={() => setIsModalDeleteAuction(false)}
                         title={"Delete auction"} body={"Do you really want to delete this auction"}
                         action={handleDeleteAuction}/>
            <ModalDialog show={isModalDeleteLot} hide={() => setIsModalDeleteLot(false)} title={"Delete lot"}
                         body={"Do you really want to delete lot " + currentLot.name + " ?"}
                         action={() => handleDeleteLot(currentLot.id)}/>
            <ModalDialog show={isModalStartCountDown} hide={() => setIsModalStartCountDown(false)}
                         title={"Start countdown"}
                         body={"The auction status will be changed to \'WAITING\' and you will not be able to change settings or add / delete lots!"}
                         action={handleStartCountdown}/>
        </Container>

    )
}
export default Auction;