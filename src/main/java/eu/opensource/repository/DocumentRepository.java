package eu.opensource.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import eu.opensource.model.Document;

/**
 * Spring Data JPA repository for entity "Document" 
 * 
 * @author Paolo Bertin
 *
 */
@Repository 
public interface DocumentRepository extends JpaRepository<Document, Long> {

    Page<Document> findBySummary(String summary, Pageable pageable);

    Page<Document> findByFilename(String filename, Pageable pageable);

    Page<Document> findByAuthor(String author, Pageable pageable);

    Page<Document> findBySpeaker(String speaker, Pageable pageable);

    Page<Document> findByDocumentyear(Integer documentyear, Pageable pageable);

}