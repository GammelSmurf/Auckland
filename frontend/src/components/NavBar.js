import React, {useEffect, useRef, useState} from "react";
import AuthService from "../services/AuthService";
import {Navbar, Nav, Container} from 'react-bootstrap';
import SockJsClient from "react-stomp";
import Notifications from "./Notifications";
import UserService from "../services/UserService";
import {faUser, faUsers, faSignOutAlt, faBullhorn} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

const MyNavBar = (props) => {
    const [notifications, setNotifications] = useState([]);
    const [notificationCounter, setNotificationCounter] = useState(0);
    const [newNotificationsIds, setNewNotificationsIds] = useState([]);
    const currentUser = AuthService.getCurrentUser();
    const [balance, setBalance] = useState();
    const client = useRef(null);

    useEffect(() => {
        UserService.getUserNotifications().then((response)=>{
            setNotifications(response.data);
        });
        UserService.getUser(currentUser.username).then((response)=>setBalance(response.data.money));
    }, [])

    const logOut = () => {
        AuthService.logout();
    }

    const onReceiveWebSocketMessage = (msg) => {
        if(msg.id){
            setNotificationCounter(notificationCounter+1);
            setNotifications(notifications.concat(msg));
            setNewNotificationsIds(newNotificationsIds.concat(msg.id));
        }
        else{
            setBalance(msg);
        }

    }

    return(
        <div>
            <SockJsClient url='http://localhost:8080/ws'
                          topics={['/user/notifications/' + currentUser.id, '/auction/balance/' + currentUser.username]}
                          onMessage={onReceiveWebSocketMessage}
                          ref={client}/>
            <Navbar bg="light" variant="light" expand="sm" className="navHeader">
                <Container>
                    <Navbar.Brand href="/home">Auckland</Navbar.Brand>
                    <Notifications notifications={notifications} counter={notificationCounter} history={props.history}
                                   reset={() => {setNotificationCounter(0); setNewNotificationsIds([])}} newNotificationsIds={newNotificationsIds}/>
                    <div><p style={{margin: 0}}>Wallet: <span style={{color: '#00AA8D'}}>{balance}$</span></p></div>
                    <Navbar.Toggle aria-controls="basic-navbar-nav" />
                    <Navbar.Collapse id="basic-navbar-nav">
                        <Nav style={{marginLeft: "auto"}}>
                            {/*<Nav.Link href="/home">Home</Nav.Link>*/}
                            <Nav.Link href="/auctions">
                                <FontAwesomeIcon icon={faBullhorn} size="sm" /> Auctions
                            </Nav.Link>
                            <Nav.Link href={"/profile/" + currentUser.username}>
                                <FontAwesomeIcon icon={faUser} size="sm" /> {currentUser.username}
                            </Nav.Link>
                            {currentUser.roles.includes('ADMIN') &&
                            <Nav.Link href="/users">
                                <FontAwesomeIcon icon={faUsers} size="sm" /> Users
                            </Nav.Link>}
                            <Nav.Link href="/auth/signin" onClick={logOut}>
                                <FontAwesomeIcon icon={faSignOutAlt} size="sm" /> Logout
                            </Nav.Link>
                        </Nav>
                    </Navbar.Collapse>
                </Container>
            </Navbar>
        </div>
    )
}
export default MyNavBar;