import {Carousel} from "react-bootstrap";
import React from "react";


const LotCarousel = (props) => {

    return(
            <Carousel>
                {props.lots.map(lot =>
                    <Carousel.Item key={lot.id}>
                        <div>
                            <img
                                className="d-block w-100"
                                src={lot.pictureLink}
                                height="300"
                                alt="No image"
                                style={{filter: "brightness(60%)", objectFit: "cover"}}
                                onError={(e)=>e.target.src = 'https://st3.depositphotos.com/23594922/31822/v/600/depositphotos_318221368-stock-illustration-missing-picture-page-for-website.jpg'}
                            />
                        </div>
                        <div style={{position: "absolute", top: 0, left: "15%", right: "15%"}}>
                            {props.status === 'FINISHED' ?
                            <h3 style={{color: "white"}}>{'Winner: ' + (lot.winner ? lot.winner.username : 'nobody')}</h3> :
                                <h3 style={{color: "white"}}>{'Opening price: ' + lot.minPrice} $</h3>}
                        </div>
                        <Carousel.Caption>
                            <h3>{lot.name}</h3>
                            <p style={{
                                overflow: 'hidden',
                                textOverflow: 'ellipsis',
                                display: '-webkit-box',
                                WebkitLineClamp:3,
                                WebkitBoxOrient:'vertical'}}>{lot.description}</p>
                        </Carousel.Caption>
                    </Carousel.Item>
                )}

            </Carousel>
    )
}

export default LotCarousel;