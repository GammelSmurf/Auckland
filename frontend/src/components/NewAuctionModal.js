import React, {useEffect, useRef, useState} from "react";
import {Modal, Form, Button} from "react-bootstrap";
import AuthService from "../services/AuthService";
import AuctionService from "../services/AuctionService";
import AucForm from "./AucForm";
import LotForm from "./LotForm";
import LotService from "../services/LotService";

const NewAuctionModal = (props) => {
    const [validated, setValidated] = useState(false);
    const [step, setStep] = useState(1);
    //const [auctionId, setAuctionId] = useState(1);
    const currentUser = AuthService.getCurrentUser();

    const parseDateTime = () => {
        return props.minDateTime.toISOString().slice(0,-5).split('T').join(' ')
    }

    const [values, setValues] = useState({
        userId: currentUser.id,

        aucName: "",
        usersLimit: "100",
        beginDate: parseDateTime(),
        lotDuration: "00:30:00",
        boostTime: "00:10:00",

        lotName: "",
        description: "",
        minBank: "1",
        step: "1",
        picture: "",
        auctionId: ""
    });

    const nextStep = () => {
        setStep(step + 1);
        setValidated(false);
    };

    const handleChange = (name) => (e) => {
        setValues({ ...values, [name]: e.target.value });
    };

    const handleSubmit = (event) => {
        const form = event.currentTarget;
        if (form.checkValidity() === false) {
            setValidated(true);
        }
        else if(step === 1){
             nextStep();
        }
        else{
            AuctionService.createAuction(values).then((response) =>
                values["auctionId"] = response.data.id
            ).then(
                () => LotService.createLot(values).then(props.hide())
            );
        }
        event.preventDefault();
        event.stopPropagation();
    }

    return (
        <Modal show={props.show} onHide={props.hide}
               animation={true}>
            <Modal.Header>
                <Modal.Title>{step === 1 ? "New auction" : "New lot"}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form noValidate validated={validated} onSubmit={handleSubmit}>
                    {
                        {
                            1: <AucForm handleChange={handleChange} minDateTime={parseDateTime()}/>,
                            2: <LotForm handleChange={handleChange}/>,
                        }[step]
                    }
                    {step === 1 ? (
                            <Button variant="warning" type="submit" style={{marginTop: "10px"}}>
                                Next
                            </Button>
                        ) :
                            <Button variant="warning" type="submit" style={{marginTop: "10px"}}>
                                Submit
                            </Button>
                        }
                </Form>

            </Modal.Body>
        </Modal>
    )
}

export default NewAuctionModal;