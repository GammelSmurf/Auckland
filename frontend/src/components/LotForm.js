import {Button, Form} from "react-bootstrap";
import React, {useState} from "react";

const LotForm = (props) => {
    return (
            <div>
                <Form.Control style={{display: "none"}} />
                <Form.Group>
                    <Form.Label>
                        Name
                    </Form.Label>
                    <Form.Control required onChange={props.handleChange("lotName")}/>
                    <Form.Control.Feedback type="invalid">
                        The value cannot be empty
                    </Form.Control.Feedback>
                </Form.Group>
                <Form.Group>
                    <Form.Label>
                        Description
                    </Form.Label>
                    <Form.Control as="textarea" required onChange={props.handleChange("description")}/>
                    <Form.Control.Feedback type="invalid">
                        The value cannot be empty
                    </Form.Control.Feedback>
                </Form.Group>
                {/*<Form.Group>
                    <Form.Label>
                        Picture
                    </Form.Label>
                    <Form.Control required type="file"/>
                    <Form.Control.Feedback type="invalid">
                        The value cannot be empty
                    </Form.Control.Feedback>
                </Form.Group>*/}
                <Form.Group>
                    <Form.Label>
                        Start bank
                    </Form.Label>
                    <Form.Control required type="number" min={1} defaultValue={1} onChange={props.handleChange("minBank")}/>
                    <Form.Control.Feedback type="invalid">
                        The min value is 1
                    </Form.Control.Feedback>
                </Form.Group>
                <Form.Group>
                    <Form.Label>
                        Minimal bet
                    </Form.Label>
                    <Form.Control required type="number" min={1} defaultValue={1} onChange={props.handleChange("step")}/>
                    <Form.Control.Feedback type="invalid">
                        The min value is 1
                    </Form.Control.Feedback>
                </Form.Group>
            </div>
    )
}

export default LotForm;