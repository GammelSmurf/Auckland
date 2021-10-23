import {Button, Form} from "react-bootstrap";
import React, {useState} from "react";
import AuthService from "../services/AuthService";

const Register = (props) => {
    const [validated, setValidated] = useState(false);
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [email, setEmail] = useState('');
    const [message, setMessage] = useState('');
    const [responseTextVisibility, setResponseTextVisibility] = useState(false);

    const handleSubmit = (event) => {
        const form = event.currentTarget;
        if (form.checkValidity() === false) {
            setValidated(true);
        }
        else {
            AuthService.register(username, password, email)
                .then(
                    (response) => {
                        if(response.status === 400){
                            setResponseTextVisibility(false);
                            setMessage(response.data);
                            setResponseTextVisibility(true);
                        }
                        else if(response.status === 200){
                            props.history.push('/auth/signin')
                        }

                    }
                );
        }
        event.preventDefault();
        event.stopPropagation();
    }

    const onChangeUsername = (e) => {
        setUsername(e.target.value);
    };

    const onChangePassword = (e) => {
        setPassword(e.target.value);
    };

    const onChangeEmail = (e) => {
        setEmail(e.target.value);
    };

    return (
        <div className="container">
            <div className="wrapper">
                <div className="standardPageHeader">
                    <h2>Please sign up</h2>
                    <hr />
                </div>
                <Form noValidate validated={validated} onSubmit={handleSubmit} className="col-sm-4">
                    <Form.Group controlId="email">
                        <Form.Label><b>Email</b></Form.Label>
                        <Form.Control
                            required
                            type="email"
                            onChange={onChangeEmail}
                        />
                        <Form.Control.Feedback type="invalid">
                            Incorrect value!
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group controlId="username">
                        <Form.Label><b>Username</b></Form.Label>
                        <Form.Control
                            required
                            type="text"
                            onChange={onChangeUsername}
                        />
                        <Form.Control.Feedback type="invalid">
                                Incorrect value!
                            </Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group controlId="password">
                        <Form.Label><b>Password</b></Form.Label>
                        <Form.Control
                            required
                            type="password"
                            onChange={onChangePassword}
                        />
                        <Form.Control.Feedback type="invalid">
                                Incorrect value!
                            </Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group>
                        {responseTextVisibility &&
                            <Form.Text className="text-danger responseText">
                                <b>{message}</b>
                            </Form.Text>
                        }
                    </Form.Group>
                    <Button type="submit" variant="warning" className="authSubmitButton">Submit</Button>
                </Form>
            </div>
        </div>
    )
}

export default Register;