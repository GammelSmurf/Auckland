import {Button, Form, FormControl, InputGroup, Modal} from "react-bootstrap";
import React, {useState} from "react";
import AuthService from "../services/AuthService";
import UserService from "../services/UserService";


const ModalGiveCash = (props) => {
    const [validated, setValidated] = useState(false);
    const [responseMessage, setResponseMessage] = useState();
    const [sum, setSum] = useState(0);

    const handleChange = (e) => {
        setSum(e.target.value);
    }

    const handleSubmit = (event) => {
        const form = event.currentTarget;
        if (form.checkValidity() === false) {
            setValidated(true);
        }
        else {
            UserService.giveCash(props.username, sum)
                .then(
                    () => {
                        setResponseMessage('Added successfully');
                    },
                    () => {
                        setResponseMessage('Error');
                    }
                );
        }
        event.preventDefault();
        event.stopPropagation();
    };

    return(
        <Modal show={props.show} onHide={props.hide}
               animation={true}>
            <Modal.Header closeButton>
                <Modal.Title>Give cash to {props.username}</Modal.Title>
            </Modal.Header>

            <Modal.Body>
                <Form noValidate validated={validated} onSubmit={handleSubmit}>
                    <Form.Group>
                        <InputGroup>
                            <InputGroup.Text>$</InputGroup.Text>
                            <FormControl required type='number' placeholder="Enter the amount" onChange={handleChange} min={1}/>
                            <InputGroup.Text>.00</InputGroup.Text>
                        </InputGroup>
                        <Form.Control.Feedback type="invalid">
                            Incorrect value
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Form.Text className="text-danger responseText">
                        <b>{responseMessage}</b>
                    </Form.Text>

                    <div style={{textAlign: 'right'}}>
                        <Button variant="warning" type="submit">Add</Button>
                    </div>
                </Form>
            </Modal.Body>
        </Modal>
    )
}

export default ModalGiveCash;