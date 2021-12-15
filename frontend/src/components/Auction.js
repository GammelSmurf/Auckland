import React, {useEffect, useRef, useState} from "react";
import {Button, Col, Container, Form, FormControl, InputGroup, Row, Spinner, Tabs} from "react-bootstrap";
//import confetti from 'canvas-confetti';

import AuctionService from "../services/AuctionService";
import LotService from "../services/LotService";
import LotCarousel from "./LotCarousel";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome'
import {faArrowLeft} from '@fortawesome/free-solid-svg-icons'
import EditAucForm from "./EditAucForm";
import AuthService from "../services/AuthService";
import EditLotModal from "./EditLotModal";
import ModalDialog from "./ModalDialog";
import BidService from "../services/BidService";
import SockJsClient from 'react-stomp';
import Categories from "./Categories";
import Chat from "./Chat";
import MessageService from "../services/MessageService";
import {animateScroll} from "react-scroll";
import {Tab} from "bootstrap";
import Members from "./Members";

const Auction = (props) => {
    const [validated, setValidated] = useState(false);
    const [auction, setAuction] = useState({});
    const [lots, setLots] = useState([]);
    const [carouselCurrentLot, setCarouselCurrentLot] = useState([]);
    const [onEdit, setOnEdit] = useState(false);
    const [strTimer, setStrTimer] = useState(null);
    const currentUser = AuthService.getCurrentUser();
    const [isSubscribed, setIsSubscribed] = useState(false);
    const [aucValues, setAucValues] = useState({});
    const [lotValues, setLotValues] = useState({});
    const [isLotModalActive, setIsLotModalActive] = useState(false);
    const [currentLot, setCurrentLot] = useState({});
    const [isModalDeleteAuction, setIsModalDeleteAuction] = useState(false);
    const [isModalDeleteLot, setIsModalDeleteLot] = useState(false);
    const [isModalStartCountDown, setIsModalStartCountDown] = useState(false);
    const [isModalSubscribe, setIsModalSubscribe] = useState(false);
    const [status, setStatus] = useState('');
    const [logs, setLogs] = useState([]);
    const [finishTime, setFinishTime] = useState('');
    const [currentPrice, setCurrentPrice] = useState('');
    const [bidAmount, setBid] = useState();
    const [creator, setCreator] = useState({});
    const [isErrorMessage, setIsErrorMessage] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const [chatMessages, setChatMessages] = useState([]);
    const [members, setMembers] = useState([]);
    const [isTimeRaise, setIsTimeRaise] = useState(false);

    const client = useRef(null);
    const timerId = useRef(null);
    //const confettiTimerId = useRef(null);
    const lotSection = useRef(null);
    const bidInput = useRef(null);

    const errorPictureURL = 'https://st3.depositphotos.com/23594922/31822/v/600/depositphotos_318221368-stock-illustration-missing-picture-page-for-website.jpg';

    const colors = [
        '#FF9E00', '#FF4545', '#40E687','#00C1FF','#AD61FF'
    ]

    const getColor = (catId) => {
        return colors[catId%colors.length];
    }

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

    const parseMessageTime = (inputDate) => {
        const date = new Date(inputDate);
        const options = {
            hour: '2-digit', minute: '2-digit'
        };
        const dateTimeFormat = new Intl.DateTimeFormat('ru-RU', options).format;
        return dateTimeFormat(date);
    }

    //------------------------WS logic------------------------//

    const syncState = (aucId) => {
        BidService.getTime(aucId).then(
            (syncResponse) => {
                if(syncResponse.data.auctionStatus === 'RUNNING'){
                    setCarouselCurrentLot([syncResponse.data.currentLot]);
                    console.log(syncResponse);
                    (syncResponse.data.amount && !syncResponse.data.changed) ?
                        setCurrentPrice(syncResponse.data.amount) :
                        setCurrentPrice(syncResponse.data.currentLot.minPrice);
                }
                if(syncResponse.data.auctionStatus === 'FINISHED'){
                    AuctionService.getAuction(auction.id).then((response)=> {
                            setAuction(response.data);
                            setFinishTime(response.data.endDateTime);
                            setLots(response.data.lots);
                        }
                    );
                } else {
                    activateTimer(syncResponse.data.secondsUntil, aucId);
                }
                setStatus(syncResponse.data.auctionStatus);
            }
        )
    }

    const activateTimer = (inputSeconds, aucId) => {
        clearInterval(timerId.current);
        let time = inputSeconds;
        timerId.current = setInterval(() => {
            const hours = time / 3600 % 60;
            const minutes = time / 60 % 60;
            const seconds = time % 60;
            if (time <= 0) {
                clearInterval(timerId.current);
                syncState(aucId);
                //animateConfetti();
            } else {
                setStrTimer(`${addZeroBefore(Math.trunc(hours))}:${addZeroBefore(Math.trunc(minutes))}:${addZeroBefore(seconds)}`);
            }
            --time;
        }, 1000);
    }

    const onReceiveWebSocketMessage = (response) => {
        if(response.username){
            setChatMessages(chatMessages.concat({...response, dateTime: parseMessageTime(response.dateTime)}))
            scrollToBottom();
        }
        if(response.amount){
            setCurrentPrice(response.amount);
            activateTimer(response.secondsUntil, auction.id);
        }
        if(response.message && !response.username){
            setLogs(logs.concat(response));
        }
    }

    const onBidChange = (e) => {
        setBid(e.target.value);
    }

    const handleSendBid = () => {
        const sum = currentPrice + carouselCurrentLot[0].priceIncreaseMinStep;
        if(!bidAmount || bidAmount < sum){
            showErrorMessage('The bid must be greater then ' + sum + '$');
        }
        else{
            setIsErrorMessage(false);
            setIsTimeRaise(true);
            setTimeout(()=>setIsTimeRaise(false), 1000);
            client.current.sendMessage('/app/play/'+auction.id, JSON.stringify({username: currentUser.username, amount: bidAmount}));
        }
        bidInput.current.value = '';
    }

    const handleSendMessage = (message) => {
        client.current.sendMessage('/app/send/'+auction.id,
            JSON.stringify({senderUsername: currentUser.username, auctionId: auction.id, dateTime: parseResponseDate(new Date()), message: message}));
    }

    //------------------------WS logic------------------------//

    /*const animateConfetti = () => {
        let ms = 4000;
        confettiTimerId.current = setInterval(() => {
        if(ms < 0){
            clearInterval(confettiTimerId.current);
        }
            confetti({
                particleCount: 2,
                angle: 60,
                spread: 55,
                origin: {x: 0},
                colors: ['#bb0000', '#ffffff']
            });
            confetti({
                particleCount: 2,
                angle: 120,
                spread: 55,
                origin: {x: 1},
                colors: ['#bb0000', '#ffffff']
            });
            ms -= 20;
        }, 20
        );
    }*/

    useEffect(() => {
        AuctionService.getAuction(props.match.params.id).then(
            (response) => {
                console.log('Auction',response.data)
                setCreator(response.data.creator);
                setIsSubscribed(response.data.subscribedUsers.some(user=>user.id === currentUser.id));
                setMembers(response.data.subscribedUsers);
                switch (response.data.status) {
                    case 'DRAFT':
                        setStatus(response.data.status);
                        setStrTimer(parseDateToDisplay(response.data.beginDateTime));
                        break;
                    case 'WAITING':
                        syncState(response.data.id);
                        break;
                    case 'RUNNING':
                        syncState(response.data.id);
                        break;
                    case 'FINISHED':
                        setStatus(response.data.status);
                        setFinishTime(response.data.endDateTime);
                        break;
                }
                setAuction({...response.data, beginDateTime: parseAuctionDate(response.data.beginDateTime)});
                setAucValues({
                    username: currentUser.username,
                    id: response.data.id,
                    name: response.data.name,
                    description: response.data.description,
                    usersCountLimit: response.data.usersCountLimit,
                    beginDateTime: parseResponseDate(response.data.beginDateTime),
                    lotDurationTime: response.data.lotDurationTime,
                    extraTime: response.data.extraTime,
                    status: response.data.status
                });
                setLots(response.data.lots);
                AuctionService.getAuctionLogs(response.data.id).then(
                    response => setLogs(response.data)
                );
                MessageService.getMessagesByAuctionId(response.data.id).then((response)=>{
                    let dataPrev = []
                    response.data.forEach(message=>
                        dataPrev.push(
                            {...message, dateTime: parseMessageTime(message.dateTime)}
                        )
                    )
                    setChatMessages(dataPrev);
                });
            }
        );
        return () => clearInterval(timerId.current);
    }, []);

    const handleStartCountdown = () => {
        AuctionService.setStatusWaiting(auction.id).then(()=>{
            setIsModalStartCountDown(false);
            syncState(auction.id);
        }).catch((error)=>{showErrorMessage(error.response.data.message)});
    }

    const setDefaultLotValues = (currentLot) => {
        setLotValues({
            id: currentLot.id,
            name: currentLot.name,
            description: currentLot.description,
            minPrice: currentLot.minPrice,
            priceIncreaseMinStep: currentLot.priceIncreaseMinStep,
            pictureLink: currentLot.pictureLink,
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
        if (name === "beginDateTime") {
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
            AuctionService.updateAuction(aucValues).then(() => setOnEdit(false), (error) => showErrorMessage(error.response.data.message));
        }
        event.preventDefault();
        event.stopPropagation();
    }

    const handleSubscribe = () => {
        AuctionService.subscribe({username: currentUser.username, auctionId: auction.id}).then(()=>
        {
            setIsModalSubscribe(false);
            setIsSubscribed(true);
            setMembers(members.concat(currentUser));
        });
    }

    const scrollToBottom = () => {
        animateScroll.scrollToBottom({
            containerId: "messageList"
        });
    }

    const parseTimeRaise = (inputTime) => {
        console.log(inputTime)
        let result = '';
        const parts = inputTime.split(':');
        if(parts[0] !== '00'){
            result += parts[0] + 'h ';
        }
        if(parts[1] !== '00'){
            result += parts[1] + 'm ';
        }
        if(parts[2] !== '00'){
            result += parts[2] + 's ';
        }
        return result;
    }

    const showErrorMessage = (message) => {
        setErrorMessage(message);
        setIsErrorMessage(false);
        setTimeout(()=>setIsErrorMessage(true), 10);
    }

    return (
        <Container>
            <SockJsClient url='http://localhost:8080/ws'
                          topics={['/auction/logs/' + auction.id, '/auction/state/' + auction.id, '/auction/chat/' + auction.id]}
                          onMessage={(msg) => onReceiveWebSocketMessage(msg)}
                          ref={client} />
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
                                            {isErrorMessage  && <p className='text-danger responseText'
                                                style={{margin: '0', fontSize: '14px'}}>{errorMessage}</p>}
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
                                    {isSubscribed && <div style={{display: 'inline-block', marginRight: '10px'}}><h5 className='basicAnim'>You are subscribed!</h5></div>}
                                    {(status === 'WAITING' && currentUser.id !== creator.id && !isSubscribed) && <Button variant="warning" style={{marginRight: "10px"}} onClick={()=>setIsModalSubscribe(true)}>Subscribe</Button>}
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
                                <div className="auctionBlock">
                                    <h5>Description</h5>
                                    <div style={{overflowY: 'auto', height: "250px"}}>
                                        {onEdit ?
                                            <Form.Group>
                                                <FormControl as="textarea" rows={9} defaultValue={auction.description}
                                                             onChange={handleChangeAuction("description")}
                                                             style={{resize: "none"}}/>
                                            </Form.Group> :
                                            <p>{auction.description}</p>}
                                    </div>
                                </div>

                                <div className="auctionBlock mt-3">
                                    <h5>Logger</h5>
                                    <div style={{overflowY: 'auto', height: "250px"}}>
                                        {(logs.length !== 0) ? logs.map(log =>
                                            <div key={log.id}>
                                                <p style={{fontSize: '12px', margin: 0}}><b>{log.dateTime}</b></p>
                                                <p style={{fontSize: '12px', margin: 0}}>{log.message}</p>
                                                <hr size={3} style={{color: '#9434B3'}}/>
                                            </div>
                                        ) : <p style={{fontStyle: 'italic'}}>There is no log messages yet</p>}
                                    </div>
                                </div>

                            </Col>
                            <Col xs={12} md={4}>
                                <div style={{textAlign: "center", overflow: 'hidden'}}>
                                    {(status === 'DRAFT' || status === 'WAITING') &&
                                        <>
                                            <h5 className='basicAnim'>Auction starts in</h5>
                                            {onEdit ? <EditAucForm auction={auction} minDate={formatDate(new Date())} handleChange={handleChangeAuction}/>
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
                                            <div>
                                                <div style={{width: '30%', display: 'inline-block'}}/>
                                                <div style={{width: '40%', display: 'inline-block'}}>
                                                    {strTimer ? <h1>{strTimer} </h1> :
                                                        <Spinner animation="border" role="status"/>}
                                                </div>
                                                <div style={{width: '30%', display: 'inline-block', textAlign: 'left'}}>
                                                    {isTimeRaise && <span
                                                        style={{fontSize: '18px', color: '#00AA8D', fontWeight: 'bold'}}
                                                        className='timeRaiseAnim'>+{parseTimeRaise(auction.extraTime)}</span>}
                                                </div>
                                            </div>
                                        </>
                                    }
                                    {status === 'FINISHED' &&
                                        <>
                                            <h5 className='basicAnim'>Auction ended in</h5>
                                            <h1>{finishTime}</h1>
                                        </>
                                    }
                                    <div className="mt-1">
                                        <LotCarousel lots={status === 'RUNNING' ? carouselCurrentLot : lots} status={status} auction={auction} parseTimeRaise={parseTimeRaise} errorPictureURL={errorPictureURL}/>
                                    </div>
                                    <div className="mt-1">
                                        {status === 'DRAFT' && (!onEdit &&
                                            <Button variant="warning"
                                                    style={{margin: "20px 0px", padding: "10px 30px"}}
                                                    onClick={() => setIsModalStartCountDown(true)}>
                                                Start countdown
                                            </Button>)}
                                        {(status === 'WAITING' && auction.tags.length > 0) &&
                                            <div>
                                                <h5>Tags</h5>
                                                <div>
                                                    {auction.tags.map(tag =>
                                                        <div className='tagWrapper'>
                                                            <span style={{borderBottom: '2px solid '+ getColor(tag.categoryId)}}>{tag.name}</span>
                                                        </div>
                                                    )}
                                                </div>
                                            </div>}
                                    </div>
                                    {status === 'RUNNING' &&
                                    <div className='basicAnim' style={{marginTop: "10px"}}>
                                        <h5>Current price</h5>
                                        <h1>{currentPrice}$ <span style={{fontSize: '20px', color: '#00AA8D'}}>(+{currentPrice-carouselCurrentLot[0].minPrice}$)</span></h1>

                                        {isSubscribed &&
                                        <div style={{padding: '5px'}}>
                                            <div style={{height: '62px'}}>
                                                <InputGroup>
                                                    <FormControl aria-label="Amount (to the nearest dollar)" ref={bidInput}
                                                                 onKeyDown={(e) => e.key === 'Enter' && handleSendBid()}
                                                                 placeholder="Enter the amount" onChange={onBidChange} type='number'/>
                                                    <InputGroup.Text>$</InputGroup.Text>
                                                </InputGroup>
                                                {isErrorMessage &&
                                                <p className='text-danger responseText'>{errorMessage}</p>}
                                            </div>
                                            <Button onClick={handleSendBid} variant="warning"
                                                    style={{marginTop: "10px", padding: "10px 30px"}}>
                                                Make a bid
                                            </Button>
                                        </div>}

                                    </div>}
                                </div>
                            </Col>
                            <Col xs={12} md={4}>
                                <div>
                                    {(status === 'DRAFT' && auction.id) ?
                                        <Categories auction={auction} getColor={getColor}/>
                                        :
                                        <div className="auctionBlock" style={{height: "640px", backgroundColor: '#E6EBEE'}}>
                                            <Tabs defaultActiveKey="chat" id="chatTabs" className="mb-3">
                                                <Tab eventKey="chat" title="Chat" onClick={()=>scrollToBottom()}>
                                                    <Chat user={currentUser} messages={chatMessages} handleChatMessage={handleSendMessage}/>
                                                </Tab>
                                                <Tab eventKey="members" title="Members">
                                                    <Members owner={auction.creator} members={members}/>
                                                </Tab>
                                            </Tabs>
                                        </div>
                                    }

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
                                        <img alt="No image" src={lot.pictureLink ? lot.pictureLink : errorPictureURL}
                                             style={{width: "100%", maxHeight: "260px", objectFit: "cover"}}
                                        onError={(e)=>e.target.src=errorPictureURL}/>
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
            <ModalDialog show={isModalStartCountDown} hide={() => {setIsModalStartCountDown(false); setErrorMessage(null)}}
                         title={"Start countdown"}
                         body={"The auction status will be changed to \'WAITING\' and you will not be able to change settings or add / delete lots!"}
                         action={handleStartCountdown} errorMessage={errorMessage}/>
            <ModalDialog show={isModalSubscribe} hide={() => setIsModalSubscribe(false)}
                         title={"Subscribe"}
                         body={`Do you want to subscribe on auction ${auction.name}?`}
                         action={handleSubscribe}/>
        </Container>

    )
}
export default Auction;