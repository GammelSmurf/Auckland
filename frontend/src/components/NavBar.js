import React, {useEffect, useState} from "react";
import AuthService from "../services/AuthService";
import {Navbar, Nav, Container} from 'react-bootstrap';
import SockJsClient from "react-stomp";
import Notifications from "./Notifications";
import UserService from "../services/UserService";

const MyNavBar = (props) => {
    const [notifications, setNotifications] = useState([]);
    const [notificationCounter, setNotificationCounter] = useState(0);
    const [newNotificationsIds, setNewNotificationsIds] = useState([]);
    const currentUser = AuthService.getCurrentUser();

    useEffect(() => {
        UserService.getUserNotifications().then((response)=>{
            setNotifications(response.data);
        });
    }, [])

    const logOut = () => {
        AuthService.logout();
    }

    const onReceiveWebSocketMessage = (msg) => {
        setNotificationCounter(notificationCounter+1);
        setNotifications(notifications.concat(msg));
        setNewNotificationsIds(newNotificationsIds.concat(msg.id));
    }

    return(
        <div>
            <SockJsClient url='http://localhost:8080/ws'
                          topics={['/user/notifications/' + currentUser.id]}
                          onMessage={onReceiveWebSocketMessage}/>
            <Navbar bg="light" variant="light" expand="sm" className="navHeader">
                <Container>
                    <Navbar.Brand href="/home">Auckland</Navbar.Brand>
                    <Notifications notifications={notifications} counter={notificationCounter} history={props.history}
                                   reset={() => {setNotificationCounter(0); setNewNotificationsIds([])}} newNotificationsIds={newNotificationsIds}/>
                    <div><p style={{margin: 0}}>Wallet: <span style={{color: '#00AA8D'}}>{currentUser.money}$</span></p></div>
                    <Navbar.Toggle aria-controls="basic-navbar-nav" />
                    <Navbar.Collapse id="basic-navbar-nav">
                        <Nav style={{marginLeft: "auto"}}>
                            <Nav.Link href="/home">Home</Nav.Link>
                            <Nav.Link href="/auctions">Auctions</Nav.Link>
                            {currentUser.roles.includes('ADMIN') && <Nav.Link href="/users">Users</Nav.Link>}
                            <Nav.Link href="/auth/signin" onClick={logOut}>Logout</Nav.Link>
                        </Nav>
                    </Navbar.Collapse>
                </Container>
            </Navbar>
        </div>
    )
}
export default MyNavBar;