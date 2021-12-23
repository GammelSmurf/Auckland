import React, {useEffect, useState} from "react";
import {Col, Container, Row, Button, Form} from "react-bootstrap";
import LotService from "../services/LotService";
import Lot from "./Lot";
import AuthService from "../services/AuthService";
import UserService from "../services/UserService";
import {Cell, Legend, Pie, ResponsiveContainer, PieChart, Tooltip} from "recharts";
import AuctionService from "../services/AuctionService";


const Profile = (props) => {
    const [user, setUser] = useState({});
    const [isOwner, setIsOwner] = useState(false);
    const [validated, setValidated] = useState(false);
    const [userValues, setUserValues] = useState({});
    const [isResponseText, setIsResponseText] = useState(false);
    const [wonLots, setWonLots] = useState({});
    const [isPie, setIsPie] = useState(false);

    const currentUser = AuthService.getCurrentUser();

    const [pieData, setPieData] = useState([]);

    useEffect(() => {
        UserService.getUser(props.match.params.username).then((response)=>{
            setUser(response.data);

            if(currentUser.id === response.data.id){
                setIsOwner(true);
            }

            setUserValues({
                username: response.data.username,
                email: response.data.email,
                firstName: response.data.firstName,
                secondName: response.data.secondName,
                about: response.data.about,
            });
        })

        AuctionService.getOwnAuctions().then((response)=>
        {
            if(response.data.length !== 0){
                setIsPie(true);
            }
            setPieData([
                {name: 'DRAFT', value: response.data.filter(auc=>auc.status === 'DRAFT').length},
                {name: 'WAITING', value: response.data.filter(auc=>auc.status === 'WAITING').length},
                {name: 'RUNNING', value: response.data.filter(auc=>auc.status === 'RUNNING').length},
                {name: 'FINISHED', value: response.data.filter(auc=>auc.status === 'FINISHED').length}
            ]);
        });

        LotService.getWonLots().then((response)=>{
            setWonLots({notConfirmedToTransferLots: response.data.notTransferredLots.filter(lot=>lot.winner.id !== currentUser.id),
                notConfirmedToAcceptLots: response.data.notTransferredLots.filter(lot=>lot.winner.id === currentUser.id),
                confirmedTransferredLots: response.data.transferredLots.filter(lot=>lot.winner.id !== currentUser.id),
                confirmedAcceptedLots: response.data.transferredLots.filter(lot=>lot.winner.id === currentUser.id)}
        )});
    }, []);

    const handleTransferLot = (lotId) => {
        LotService.transferLot(lotId).then((response)=>console.log(response.data));
        window.location.reload();
    }

    const handleAcceptLot = (lotId) => {
        LotService.acceptLot(lotId).then((response)=>console.log(response.data));
        window.location.reload();
    }

    const handleChangeSettings = (event) => {
        const form = event.currentTarget;
        if (form.checkValidity() === false) {
            setValidated(true);
        }
        else {
            setIsResponseText(false);
            UserService.updateUser(userValues).then(()=>{setIsResponseText(true)});
        }
        event.preventDefault();
        event.stopPropagation();
    }

    const onChangeUser = (name) => (e) => {
        let value = e.target.value;
        setUser({...user, [name]: value})
        setUserValues({...userValues, [name]: value});
    };

    const pieColors = [{
        id: 0,
        color: '#AAAAAA'
    }, {
        id: 1,
        color: '#FFC107'
    },{
        id: 2,
        color: '#91D0A9'
    },{
        id: 3,
        color: '#9434B3'
    }]

    const isBlendStroke = () => {
        let flag;
        pieData.filter(item => item.value === 0).length === 2 ? flag = true : flag = false;
        return flag;
    }

    const renderCustomizedLabel = ({ cx, cy, midAngle, innerRadius, outerRadius, index }) => {
        const RADIAN = Math.PI / 180;
        const radius = innerRadius + (outerRadius - innerRadius) * 0.5;
        const x = cx + radius * Math.cos(-midAngle * RADIAN);
        const y = cy + radius * Math.sin(-midAngle * RADIAN);

        return (
            <text x={x} y={y} fill="white" textAnchor={x > cx ? 'start' : 'end'} dominantBaseline="central" fontSize={20}>
                {pieData[index].value > 0 && pieData[index].value}
            </text>
        );
    };

    return(
        <Container style={{paddingBottom: '80px'}}>
            <Row>
                <div className="standardPageHeader">
                    <h2>Profile</h2>
                    <hr />
                </div>
                <Col xs={4}>
                    {isOwner ?
                    <div style={{overflow: "hidden", padding: '5px'}}>
                        <Form noValidate validated={validated} onSubmit={handleChangeSettings} className="basicAnim">
                            <Form.Group controlId="username">
                                <Form.Label><b>Username</b></Form.Label>
                                <Form.Control
                                    required
                                    type="text"
                                    onChange={onChangeUser("username")}
                                    defaultValue={user.username}
                                    disabled
                                />
                            </Form.Group>

                            <Form.Group controlId="email">
                                <Form.Label><b>Email</b></Form.Label>
                                <Form.Control
                                    required
                                    type="email"
                                    onChange={onChangeUser("email")}
                                    defaultValue={user.email}
                                    disabled
                                />
                            </Form.Group>
                            <Form.Group controlId="firstName">
                                <Form.Label><b>First Name</b></Form.Label>
                                <Form.Control
                                    type="text"
                                    onChange={onChangeUser("firstName")}
                                    defaultValue={user.firstName}
                                />
                            </Form.Group>
                            <Form.Group controlId="secondName">
                                <Form.Label><b>Second Name</b></Form.Label>
                                <Form.Control
                                    type="text"
                                    onChange={onChangeUser("secondName")}
                                    defaultValue={user.secondName}
                                />
                            </Form.Group>
                            <Form.Group controlId="about">
                                <Form.Label><b>About</b></Form.Label>
                                <Form.Control
                                    as="textarea" rows={4}
                                    type="text"
                                    onChange={onChangeUser("about")}
                                    defaultValue={user.about}
                                />
                            </Form.Group>
                            <Form.Group style={{height: '20px'}}>
                            {isResponseText &&
                            <Form.Text className="text-danger responseText">
                                <b>Changed successfully!</b>
                            </Form.Text>
                            }
                        </Form.Group>
                            <Button type="submit" variant="warning" className="authSubmitButton">Submit</Button>
                        </Form>
                    </div>
                        :
                    <div>
                        <h5>Username</h5>
                        <p>{user.username}</p>
                        <h5>Email</h5>
                        <p>{user.email}</p>
                        {user.firstName &&
                            <>
                                <h5>First Name</h5>
                                <p>{user.firstName}</p>
                            </>
                        }
                        {user.secondName &&
                        <>
                            <h5>Second Name</h5>
                            <p>{user.secondName}</p>
                        </>
                        }
                        {user.about &&
                        <>
                            <h5>About</h5>
                            <p>{user.about}</p>
                        </>
                        }
                    </div>}

                </Col>

                {isPie && isOwner &&
                <Col>
                    <h5 style={{textAlign: 'center'}}>My auctions</h5>
                    <ResponsiveContainer width={'100%'} height={260}>
                        <PieChart>
                            <Pie data={pieData} dataKey="value" nameKey="name" cx="50%" cy="50%"
                                 outerRadius={80} fill="#8884d8" paddingAngle={0} label={renderCustomizedLabel}
                                 labelLine={false} isAnimationActive={false} blendStroke={isBlendStroke()}>
                                {
                                    pieData.map((entry, index) => (
                                        <Cell key={`cell-${index}`} fill={pieColors[index].color}/>
                                    ))
                                }
                            </Pie>
                            <Legend verticalAlign="bottom"/>
                            <Tooltip/>
                        </PieChart>
                    </ResponsiveContainer>
                </Col>}

            </Row>

            {isOwner &&
            <>
                <Row>
                    <div className="standardPageHeader">
                        <h2>Not confirmed lots</h2>
                        <hr/>
                    </div>
                    <div style={{padding: '10px'}}>
                        <h3>To transfer</h3>
                        {(wonLots.notConfirmedToTransferLots && wonLots.notConfirmedToTransferLots.length !== 0) ? wonLots.notConfirmedToTransferLots.map(lot =>
                            <Lot key={lot.id} lot={lot} action={handleTransferLot}
                                 isWaiting={lot.sellerTransferConfirmation}/>
                        ) : <p className='fst-italic'>There is no lots to confirm</p>}
                    </div>
                    <div style={{padding: '10px'}}>
                        <h3>To accept</h3>
                        {(wonLots.notConfirmedToAcceptLots && wonLots.notConfirmedToAcceptLots.length !== 0) ? wonLots.notConfirmedToAcceptLots.map(lot =>
                            <Lot key={lot.id} lot={lot} action={handleAcceptLot}
                                 isWaiting={lot.buyerAcceptConfirmation}/>
                        ) : <p className='fst-italic'>There is no lots to confirm</p>}
                    </div>
                </Row>
                <Row>
                    <div className="standardPageHeader">
                        <h2>Confirmed lots</h2>
                        <hr/>
                    </div>
                    <div style={{padding: '10px'}}>
                        <h3>Transferred</h3>
                        {(wonLots.confirmedTransferredLots && wonLots.confirmedTransferredLots.length !== 0) ? wonLots.confirmedTransferredLots.map(lot =>
                            <Lot key={lot.id} lot={lot} isConfirmed/>
                        ) : <p className='fst-italic'>There is no transferred lots yet</p>}
                    </div>
                    <div style={{padding: '10px'}}>
                        <h3>Accepted</h3>
                        {(wonLots.confirmedAcceptedLots && wonLots.confirmedAcceptedLots.length !== 0) ? wonLots.confirmedAcceptedLots.map(lot =>
                            <Lot key={lot.id} lot={lot} isConfirmed/>
                        ) : <p className='fst-italic'>There is no accepted lots yet</p>}
                    </div>
                </Row>
            </>}

        </Container>
    )
}

export default Profile;