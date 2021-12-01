import React from "react";
import {Button, Modal} from "react-bootstrap";


const ModalDialog = (props) => {

    return(
        <Modal show={props.show} onHide={props.hide} animation={true}>
            <Modal.Header closeButton>
                <Modal.Title>{props.title}</Modal.Title>
            </Modal.Header>

            <Modal.Body>
                <p>{props.body}</p>
                {props.errorMessage && <p className='text-danger responseText'>{props.errorMessage}</p>}
            </Modal.Body>

            <Modal.Footer>
                <Button variant="secondary" onClick={props.hide}>Close</Button>
                <Button variant="warning" onClick={props.action}>Yes</Button>
            </Modal.Footer>
        </Modal>
    )
}

export default ModalDialog;