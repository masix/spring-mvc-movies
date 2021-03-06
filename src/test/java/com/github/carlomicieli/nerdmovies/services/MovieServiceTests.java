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
package com.github.carlomicieli.nerdmovies.services;

import com.github.carlomicieli.nerdmovies.models.Movie;
import com.github.carlomicieli.nerdmovies.utility.PaginatedResult;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Carlo Micieli
 */
@RunWith(MockitoJUnitRunner.class)
public class MovieServiceTests {

    @Mock
    private MongoTemplate mockMongo;

    @InjectMocks
    private MongoMovieService movieService;

    @Before
    public void setUp() {
        //This method has to be called to initialize annotated fields.
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetTheRecentMovies() {
        ArgumentCaptor<Query> argument = ArgumentCaptor.forClass(Query.class);
        when(mockMongo.find(isA(Query.class), eq(Movie.class))).thenReturn(new ArrayList<Movie>());

        List<Movie> l = movieService.getRecentMovies(5);

        verify(mockMongo).find(argument.capture(), eq(Movie.class));
        assertNotNull("The list of movies is empty", l);
        assertEquals(5, argument.getValue().getLimit());
        assertTrue("The sort criteria is missing",
                argument.getValue().getSortObject().containsField("savedAt"));
    }

    @Test
    public void shouldGetAllMoviesPaginated() {
        List<Movie> movies = Arrays.asList(new Movie(), new Movie());

        ArgumentCaptor<Query> argument = ArgumentCaptor.forClass(Query.class);
        when(mockMongo.find(isA(Query.class), eq(Movie.class))).thenReturn(movies);
        when(mockMongo.count(isA(Query.class), eq(Movie.class))).thenReturn((long) movies.size());
        PaginatedResult<Movie> results = movieService.getAllMovies(1, 10);

        List<Movie> data = results.getData();

        verify(mockMongo).find(argument.capture(), eq(Movie.class));
        assertEquals(10, argument.getValue().getLimit());
        assertEquals(0, argument.getValue().getSkip());

        assertNotNull("The movies list is empty", data);
        assertEquals(2, data.size());
        assertEquals(2, results.getNrOfElements());
        assertEquals(1, results.getPage());
        assertEquals(10, results.getPageSize());
        assertEquals("The first link is wrong", 1, results.getFirstPageLink());
        assertEquals("The last link is wrong", 1, results.getLastPageLink());
    }

    @Test
    public void shouldSaveMovies() {
        Movie movie = new Movie();
        movie.setTitle("BBBB");
        movieService.save(movie);
        verify(mockMongo).save(eq(movie));
    }

    @Test
    public void shouldFindMoviesById() {
        when(mockMongo.findById(isA(ObjectId.class), eq(Movie.class))).thenReturn(new Movie());
        final ObjectId id = new ObjectId();

        Movie movie = movieService.findById(id);

        verify(mockMongo).findById(eq(id), eq(Movie.class));
        assertNotNull("Movie was not found", movie);
    }

    @Test
    public void shouldFindMovieBySlug() {
        when(mockMongo.findOne(isA(Query.class), eq(Movie.class))).thenReturn(new Movie());

        Movie movie = movieService.findBySlug("the-blues-broters");
        assertNotNull("Movie was not found", movie);
    }

    @Test
    public void shouldDeleteMovies() {
        Movie movie = new Movie();
        movieService.delete(movie);
        verify(mockMongo).remove(eq(movie));
    }
}
