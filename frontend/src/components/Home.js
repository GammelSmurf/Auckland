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
                   {/* <span style={{fontSize: '14px', fontStyle: 'italic'}}>- Marie Freifrau Ebner von Eschenbach</span>*/}
                </div>

                <img src={logo} className='App-logo' alt='logo' />
                <p>Welcome, {currentUser.username}!</p>
                {/*<p>Lorem Ipsum - это текст-"рыба", часто используемый в печати и вэб-дизайне. Lorem Ipsum является стандартной "рыбой"
                    для текстов на латинице с начала XVI века. В то время некий безымянный печатник создал большую коллекцию размеров и
                    форм шрифтов, используя Lorem Ipsum для распечатки образцов. Lorem Ipsum не только успешно пережил без заметных
                    изменений пять веков, но и перешагнул в электронный дизайн.</p>*/}
                <div>
                    <Button variant='warning' style={{display: 'inline-block', marginRight: '15px'}} onClick={()=>props.history.push('/auctions')}>Auctions</Button>
                    <Button variant='warning'>Profile</Button>
                </div>
            </div>


            {/*<img src={logo} className='App-logo' alt='logo' />
            <p>Welcome, {currentUser.username}!</p>*/}
        </header>
    )
}

export default Home;