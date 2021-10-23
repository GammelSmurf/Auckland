import React, { useState} from "react";
import {Form, Button} from 'react-bootstrap'

import AuthService from "../services/AuthService";


const Login = (props) => {

    const [validated, setValidated] = useState(false);
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');
    const [responseTextVisibility, setResponseTextVisibility] = useState(false);


    const handleSubmit = (event) => {
        const form = event.currentTarget;
        if (form.checkValidity() === false) {
            setValidated(true);
        }
        else {
            AuthService.login(username, password)
                .then(
                    () => {
                        props.history.push('/home');
                    },
                    () => {
                        setResponseTextVisibility(false);
                        setMessage('Invalid username or password');
                        setResponseTextVisibility(true);
                    }
                );
        }
        event.preventDefault();
        event.stopPropagation();
    };

    const onChangeUsername = (e) => {
        const username = e.target.value;
        setUsername(username);
    };

    const onChangePassword = (e) => {
        const password = e.target.value;
        setPassword(password);
    };

    return (
            <div className="container">
                <div className="wrapper">
                    <div className="standardPageHeader">
                        <h2>Please sign in</h2>
                        <hr />
                    </div>
                    <Form noValidate validated={validated} onSubmit={handleSubmit} className="col-sm-4">
                        <Form.Group controlId="username">
                            <Form.Label><b>Username</b></Form.Label>
                            <Form.Control
                                required
                                type="text"
                                onChange={onChangeUsername}
                            />
                        </Form.Group>

                        <Form.Group controlId="password">
                            <Form.Label><b>Password</b></Form.Label>
                            <Form.Control
                                required
                                type="password"
                                onChange={onChangePassword}
                            />
                        </Form.Group>
                        <Form.Group>
                            {responseTextVisibility &&
                                <Form.Text className="text-danger responseText">
                                    <b>{message}</b>
                                </Form.Text>
                            }
                        </Form.Group>
                        <Button type="submit" variant="warning" className="authSubmitButton">Login</Button>
                    </Form>
                </div>
            </div>
    );
};

export default Login;