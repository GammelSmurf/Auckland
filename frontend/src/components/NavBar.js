import React from "react";
import AuthService from "../services/AuthService";
import { Navbar, Nav, Container } from 'react-bootstrap';

const MyNavBar = () =>{
    function logOut() {
        AuthService.logout();
    }
        return(
            <Navbar bg="light" variant="light" expand="sm" className="navHeader">
                <Container>
                    <Navbar.Brand href="/home">Auckland</Navbar.Brand>
                    <Navbar.Toggle aria-controls="basic-navbar-nav" />
                    <Navbar.Collapse id="basic-navbar-nav">
                        <Nav style={{marginLeft: "auto"}}>
                            <Nav.Link href="/home">Home</Nav.Link>
                            <Nav.Link href="#">Auctions</Nav.Link>
                            <Nav.Link href="/auth/signin" onClick={logOut}>Logout</Nav.Link>
                        </Nav>
                    </Navbar.Collapse>
                </Container>
            </Navbar>
    )
}
export default MyNavBar;