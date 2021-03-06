/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.carlomicieli.nerdmovies.controllers;

import com.github.carlomicieli.nerdmovies.models.Comment;
import com.github.carlomicieli.nerdmovies.models.Movie;
import com.github.carlomicieli.nerdmovies.services.MovieService;
import com.github.carlomicieli.nerdmovies.utility.PaginatedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.github.carlomicieli.nerdmovies.utility.ImageUtils.convert;
import static com.github.carlomicieli.nerdmovies.utility.ImageUtils.createThumbnail;

/**
 * @author Carlo Micieli
 */
@Controller
@RequestMapping("/movies")
public class MovieController {
    private MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @ModelAttribute("ratings")
    public List<String> getRatings() {
        return Arrays.asList("G", "PG", "PG-13", "R", "NC-17");
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "25") int pageSize,
            Model model) {
        PaginatedResult<Movie> results = movieService.getAllMovies(page, pageSize);
        model.addAttribute("results", results);
        return "movie/list";
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String newMovie(Model model) {
        model.addAttribute(new Movie());
        return "movie/new";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String save(@Valid @ModelAttribute Movie movie,
                       @RequestParam("file") MultipartFile file,
                       BindingResult result,
                       Model model) throws IOException {

        if (result.hasErrors()) {
            model.addAttribute(movie);
            return "movie/new";
        }

        if (!file.isEmpty()) {
            movie.setPoster(convert(file));
            movie.setThumb(createThumbnail(file, 100));
        }

        movieService.save(movie);
        return "redirect:movies";
    }

    @RequestMapping(value = "/{movieSlug}", method = RequestMethod.DELETE)
    public String delete(@PathVariable String movieSlug) {
        Movie movie = movieService.findBySlug(movieSlug);
        movieService.delete(movie);
        return "redirect:../movies";
    }

    @RequestMapping(value = "/{movieSlug}/edit", method = RequestMethod.GET)
    public String edit(@PathVariable String movieSlug, Model model) {
        Movie movie = movieService.findBySlug(movieSlug);
        model.addAttribute(movie);
        return "movie/edit";
    }

    @RequestMapping(value = "/{movie}/update", method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute Movie movie,
                         @RequestParam("file") MultipartFile file,
                         BindingResult result) throws IOException {
        if (result.hasErrors()) {
            return "movie/edit";
        }

        if (!file.isEmpty()) {
            movie.setPoster(convert(file));
            movie.setThumb(createThumbnail(file, 100));
        }

        movieService.save(movie);
        return "redirect:../../movies";
    }

    @RequestMapping(value = "/{movieSlug}", method = RequestMethod.GET)
    public String view(@PathVariable String movieSlug, Model model) {
        Movie movie = movieService.findBySlug(movieSlug);
        if (movie == null) {
            return "errors/notfound";
        }
        model.addAttribute(movie);
        model.addAttribute("newComment", new Comment());
        return "movie/view";
    }

    @RequestMapping(value = "/{movieSlug}/addcomment", method = RequestMethod.POST)
    public String addComment(@PathVariable String movieSlug,
                             @Valid @ModelAttribute Comment comment,
                             BindingResult result,
                             Model model) {

        Movie movie = movieService.findBySlug(movieSlug);

        if (result.hasErrors()) {
            model.addAttribute("newComment", comment);
            model.addAttribute(movie);
        } else {
            comment.setPostedAt(new Date());
            movie.addComment(comment);
            movieService.save(movie);

            model.addAttribute(movie);
            model.addAttribute("newComment", new Comment());
        }
        return String.format("redirect:../%s", movieSlug);
    }
}
