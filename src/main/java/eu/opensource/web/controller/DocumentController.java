package eu.opensource.web.controller;

import java.util.Optional;
import java.util.Locale;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;

import eu.opensource.web.controller.util.*;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;

import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.context.MessageSource;

import eu.opensource.services.*;
import eu.opensource.model.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/document", produces = MediaType.APPLICATION_JSON_VALUE)
@Controller
public class DocumentController {

	private final DocumentService documentService;

    @Autowired
    private MessageSource messageSource;

    /**
     * Visualizza tutti i record 
     *
     * @param page
     * @param size
     * @param uiModel
     * @return nome vista
     */
    @GetMapping("/all")
    public String listDocument(@RequestParam(name = "page", defaultValue = "0") int page,
                                   	@RequestParam(name = "size", defaultValue = "10") int size,
                                   	Model uiModel) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Document> document = documentService.getAllDocument(pageable);

        uiModel.addAttribute("page", page);
        uiModel.addAttribute("size", size);
        uiModel.addAttribute("document", document);

        log.debug("visualizza tutti i document");

        return "document/list";
    }

    /**
     * Gestisce la richiesta di visualizzare un element
     *
     * @param id
     * @param uiModel
     * @return nome vista
     */
    @GetMapping(value = "/{id}")
    public String showDocument(@PathVariable("id") Long id, Model uiModel) {

        Optional<Document> entity = documentService.getDocumentById(id);

        if (entity.isPresent()) {
            Map<String, String> document = new HashMap<>();
            Document object = entity.get();
            document.put("id", String.valueOf(object.getId()));
            document.put("summary", String.valueOf(object.getSummary()));
            document.put("filename", String.valueOf(object.getFilename()));
            document.put("author", String.valueOf(object.getAuthor()));
            document.put("speaker", String.valueOf(object.getSpeaker()));
            document.put("documentyear", String.valueOf(object.getDocumentyear()));
            document.put("content", String.valueOf(object.getContent()));

            uiModel.addAttribute("document", document);

            return "document/showDocument";
        } else {
            return "document/error";
        }
    }

    /**
     * Recupera un elemento e rimanda al form per l'aggiornamento
     *
     * @return pagina edit
     */
    @GetMapping(value = "/{id}", params = "form")
    public String updateFormDocument(@PathVariable("id") Long id,
                                           @RequestParam(required = true) String form,
                                           Model uiModel) {

        Optional<Document> document = documentService.getDocumentById(id);

        if (document.isEmpty()) {
            return "document/error";
        } else {
            uiModel.addAttribute("document", document.get());
            return "document/edit";
        }
    }

    @PutMapping()
    public String updateDocument(@ModelAttribute Document document, Model uiModel) {

        document = documentService.saveDocument(document);
        
        uiModel.addAttribute("messaggio", "Document aggiornato con successo");

        return "confermaAggiornamento"; // Mostra la pagina di conferma
    }

    /**
     * Recupera il contenuto di un elemento
     *
     * @param id identificativo elemento\
     * @return conenuto elemento
     */
    @GetMapping(value = "/content/{id}")
    @ResponseBody
    public byte[] downloadContent(@PathVariable("id") Long id) {
           
        Optional<Document> document = documentService.getDocumentById(id);

        return document.get().getContent();
    }

    /**
     * Crea elemento empty e rimanda al form per definire gli attributi
     *
     * @return pagina edit
     */
    @GetMapping(params = "form")
    public String createFormDocument(@RequestParam(required = true) String form, Model uiModel) {

        Document document = new Document();

        uiModel.addAttribute("document", document);

        return "document/edit";
    }

    /**
     * Rende persistente un nuovo elemento
     *
     * @param Document
     * @param bindingResult
     * @param uiModel
     * @param httpServletRequest
     * @param redirectAttributes
     * @param locale
     * @param file
     *
     * @return nome vista
     */
    @PostMapping()
    public String createDocument(@Valid Document document,
                                       @RequestParam(value = "file", required = false) Part file,
                                       BindingResult bindingResult,
                                       Model uiModel,
                                       HttpServletRequest httpServletRequest,
                                       RedirectAttributes redirectAttributes,
                                       Locale locale) {

        log.info("Creating Document");

        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("message", new Message("error", messageSource.getMessage("document.save.fail", new Object[]{}, locale)));
            uiModel.addAttribute("document", document);
            return "document/edit";
        }

        //
        uiModel.asMap().clear();
        redirectAttributes.addFlashAttribute("message", new Message("success", messageSource.getMessage("document.save.success", new Object[]{}, locale)));

        log.info("Document id: " + document.getId());

        // Process upload file
        if (file != null) {
            log.info("File name: " + file.getName());
            log.info("File size: " + file.getSize());
            log.info("File content type: " + file.getContentType());
            byte[] fileContent = null;
            try {
                InputStream inputStream = file.getInputStream();
                if(inputStream == null)
                    log.info("File inputstream is null");
                // fileContent = IOUtils.toByteArray(inputStream);
                document.setContent(fileContent);
            } catch (IOException ex) {
                log.error("Error saving uploaded file");
            }
            document.setContent(fileContent);
        }

        document = documentService.saveDocument(document);
        
        String url = "redirect:/document/" + UrlUtil.encodeUrlPathSegment(document.getId().toString(), httpServletRequest);

        return url;
    }

    @DeleteMapping
    public void deleteDocument(@PathVariable("id") Long id) {
        
        documentService.delete(id);
    }
}
