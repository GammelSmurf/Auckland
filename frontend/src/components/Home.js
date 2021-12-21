import React, {useEffect} from 'react';
import {Button} from 'react-bootstrap';
import logo from '../logo.svg';
import AuthService from "../services/AuthService";

const Home = (props) => {

    useEffect(() => {

    })
    const currentUser = AuthService.getCurrentUser();

    return (
        <header className='App-header' style={{textAlign: 'center'}}>
            <div>
                <h2>Auckland</h2>
                <div>
                    <p style={{fontSize: '18px', margin: 0, fontStyle: 'italic'}}>Many priceless things can be bought ...</p>
                </div>

                <img src={logo} className='App-logo' alt='logo' />
                <p>Welcome, {currentUser.username}!</p>
                <div>
                    <Button variant='warning' style={{display: 'inline-block', marginRight: '15px'}} onClick={()=>props.history.push('/auctions')}>Auctions</Button>
                    <Button variant='warning' onClick={()=>props.history.push('/profile/'+currentUser.username)}>Profile</Button>
                </div>
            </div>
        </header>
    )
}

export default Home;