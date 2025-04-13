package eu.opensource.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import eu.opensource.model.Document;

/**
 * Service for entity "Document"
 * 
 * This service provides all the necessary operations required by the REST controller
 * 
 * @author Paolo Bertin
 *
 */
public interface DocumentService {

	Optional<Document> getDocumentById(Long id);

    Page<Document> getAllDocument(Pageable pageable);
       
    Document saveDocument(Document document);

    void deleteById(Long id);
}
