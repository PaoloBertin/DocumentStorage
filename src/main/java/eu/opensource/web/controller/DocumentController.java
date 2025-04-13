


package eu.opensource.web.controller;

import java.util.List;
import java.util.Optional;
import java.util.Locale;
import java.io.IOException;
import java.io.InputStream;

import eu.opensource.web.controller.util.*;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.context.MessageSource;

import eu.opensource.services.*;
import eu.opensource.model.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/Document", produces = MediaType.APPLICATION_JSON_VALUE)
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
    public String viewAllDocument(@RequestParam(name = "page", defaultValue = "0") int page,
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
    public String viewDocument(@PathVariable("id") Long id, Model uiModel) {

        Optional<Document> document = documentService.getDocumentById(id);

        if (document.isPresent()) {
            uiModel.addAttribute("document", document.get());
            return "document/show";
        } else {
            return "document/error";
        }
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
     * Recupera un documento e rimanda al form per l'aggiornamento
     *
     * @return pagina edit
     */
    @GetMapping(value = "/{id}", params = "form")
    public String updateForm(@PathVariable("id") Long id,
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

        documentService.saveDocument(document);
        uiModel.addAttribute("messaggio", "Documento aggiornato con successo");

        return "confermaAggiornamento"; // Mostra la pagina di conferma
    }

    /**
     * Rimanda al form per creare un nuovo documento
     *
     * @return pagina edit
     */
    @GetMapping(params = "form")
    public String createForm(@RequestParam(required = true) String form,
                             Model uiModel) {

        Document document = new Document();
        uiModel.addAttribute("document", document);

        return "document/edit";
    }

    /**
     * Rende persistente un nuovo documento
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
                                 BindingResult bindingResult,
                                 Model uiModel,
                                 HttpServletRequest httpServletRequest,
                                 RedirectAttributes redirectAttributes,
                                 Locale locale,
                                 @RequestParam(value = "file", required = false) Part file) {

        log.info("Creating Document");
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("message", new Message("error", messageSource.getMessage("document.save.fail", new Object[]{}, locale)));
            uiModel.addAttribute("document", document);
            return "document/edit";
        }
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
                if (inputStream == null)
                    log.info("File inputstream is null");
                // fileContent = IOUtils.toByteArray(inputStream);
                document.setContent(fileContent);
            } catch (IOException ex) {
                log.error("Error saving uploaded file");
            }
            document.setContent(fileContent);
        }

        documentService.saveDocument(document);
        String url = "redirect:/document/" + UrlUtil.encodeUrlPathSegment(document.getId().toString(), httpServletRequest);

        return url;
    }

    /**
     * Gestisce l'arrivo di un form con i campi editati
     *
     * @param Document
     * @param bindingResult
     * @param uiModel
     * @param httpServletRequest
     * @param redirectAttributes
     * @param locale
     *
     * @return vista
     */
    @PostMapping(value = "/{id}", params = "form")
    public String update(@Valid Document document,
                         BindingResult bindingResult,
                         Model uiModel,
                         HttpServletRequest httpServletRequest,
                         RedirectAttributes redirectAttributes,
                         Locale locale,
                         @RequestParam(value = "file", required = false) Part file) {

        log.info("Updating Document");

        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("message", new Message("error", messageSource.getMessage("Document.save.fail", new Object[]{}, locale)));
            uiModel.addAttribute("Document", document);

            return "document/edit";
        }

        uiModel.asMap().clear();
        redirectAttributes.addFlashAttribute("message",
                                             new Message("success", messageSource.getMessage("Document.save.success",
                                                                                             new Object[]{}, locale)));
        // rende persistenti le modifiche
        documentService.saveDocument(document);

        String url = "redirect:/document/" + document.getId();

        return url;
	}
}
