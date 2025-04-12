package eu.opensource.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;


import java.io.Serializable;

/**
 * JPA entity class for "Document"
 *
 * @author Paolo Bertin
 *
 */
@Setter
@Getter
@Table(name = "Document")
@Entity
public class Document implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String     summary ;

    private String     filename ;

    private String     author ;

    private String     speaker ;

    private Integer    documentyear ;

}