import React, {useEffect, useState} from 'react';
import logo from '../logo.svg';
import HomeService from '../services/HomeService';
import AuthService from "../services/AuthService";

const Home = () => {
    const [message, setMessage] = useState('');
    useEffect(() => {
        HomeService.getHello().then(
            (response) => {
                setMessage(response.data)
            })
    })
    const currentUser = AuthService.getCurrentUser();
    return (
        <header className='App-header'>
            <img src={logo} className='App-logo' alt='logo' />
            <p>
                {message}
            </p>
            <p>User: {currentUser.username}</p>
        </header>
    )
}

export default Home;