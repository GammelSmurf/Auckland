import {Button, Form, Modal} from "react-bootstrap";
import LotForm from "./LotForm";
import React, {useState} from "react";


const EditLotModal = (props) => {
    const [validated, setValidated] = useState(false);

    const handleSubmit = (event) => {
        const form = event.currentTarget;
        if (form.checkValidity() === false) {
            setValidated(true);
        }
        else{
            props.handleLotSubmit();
        }
        event.preventDefault();
        event.stopPropagation();
    }

    return (
        <Modal show={props.show} onHide={props.hide}
               animation={true}>
            <Modal.Header>
                <Modal.Title>{props.lot.id ? "Edit lot" : "New lot"}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form noValidate validated={validated} onSubmit={handleSubmit}>
                    <LotForm handleChange={props.handleChange} lot={props.lot}/>
                    <Button variant="warning" type="submit" style={{marginTop: "10px"}}>
                        Submit
                    </Button>
                </Form>
            </Modal.Body>
        </Modal>
    )
}

export default EditLotModal;