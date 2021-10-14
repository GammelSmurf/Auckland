import React, {useEffect, useState} from 'react';
import logo from '../logo.svg';
import HomeService from '../Services/HomeService';

const Home = () => {
    const [message, setMessage] = useState('');
    useEffect(() => {
        HomeService.getHello().then(
            (response) => setMessage(response.data))

    })
    return (
        <header className='App-header'>
            <img src={logo} className='App-logo' alt='logo' />
            <p>
                {message}
            </p>
        </header>
    )
}

export default Home;